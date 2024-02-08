package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MealException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Meal.Meal;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.domain.Weight;
import mealplanb.server.dto.GetKcalStatisticResponse;
import mealplanb.server.dto.GetKcalStatisticResponse.DailyKcal;
import mealplanb.server.dto.weight.GetWeightStatisticResponse;
import mealplanb.server.dto.weight.WeightResponse;
import mealplanb.server.repository.MealRepository;
import mealplanb.server.repository.MealRepository.DailyKcalNativeVo;
import mealplanb.server.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static mealplanb.server.dto.GetKcalStatisticResponse.*;
import static mealplanb.server.repository.MealRepository.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticService {
    private final MemberRepository memberRepository;
    private final MealRepository mealRepository;
    private final MemberService memberService;

    /**
     *  칼로리 일간 조회
     */
    public GetKcalStatisticResponse getDailyKcals(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        LocalDate startDate = member.getCreatedAt().toLocalDate();
        LocalDate endDate = LocalDate.now();

        List<DailyKcal> result = createDailyEmptyKcalResponses(startDate, endDate);
        mealRepository.findDailyKcal(memberId)
                .ifPresent(kcals -> updateDailyKcalResponses(result, kcals));

        return new GetKcalStatisticResponse("daily", result);
    }

    private List<DailyKcal> createDailyEmptyKcalResponses(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> new DailyKcal(date, 0, 0,0,0))
                .collect(Collectors.toList());
    }

    private void updateDailyKcalResponses(List<DailyKcal> result, List<DailyKcalNativeVo> kcals) {
        kcals.forEach(day -> {
            int index = (int) ChronoUnit.DAYS.between(result.get(0).getDate(), day.getDate());
            if (index >= 0 && index < result.size()) {
                String mealIds = day.getMealIds();
                String[] mealIdsArray = mealIds.split(","); // 쉼표로 구분된 문자열을 배열로 분할

                List<Meal> meals = new ArrayList<>();
                for (String mealIdStr : mealIdsArray) {
                    Long mealId = Long.parseLong(mealIdStr.trim()); // 문자열을 Long으로 변환
                    Meal meal = mealRepository.findByMealIdAndStatus(mealId, BaseStatus.A)
                            .orElseThrow(()-> new MealException(BaseExceptionResponseStatus.MEAL_NOT_FOUND));
                    meals.add(meal);
                }
                int kcal = memberService.calculateIntakeKcal(meals);
                int[] nutrient = memberService.calculateNutrient(meals);
                result.set(index, new DailyKcal(day.getDate(), kcal, nutrient[0], nutrient[1], nutrient[2]));
            }
        });
    }

    /**
     *  칼로리 주간 조회
     */
    public GetKcalStatisticResponse getWeeklyKcals(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        LocalDate startDate = member.getCreatedAt().toLocalDate();
        LocalDate endDate = LocalDate.now();

        List<WeeklyKcal> result = createWeeklyEmptyKcalResponses(startDate, endDate);
        mealRepository.findWeeklyKcal(memberId)
                .ifPresent(kcals -> updateWeeklyKcalResponses(result, kcals));

        return new GetKcalStatisticResponse("weekly", result);
    }

    private List<WeeklyKcal> createWeeklyEmptyKcalResponses(LocalDate startDay, LocalDate endDay) {
        List<WeeklyKcal> weeklyWeights = new ArrayList<>();

        // 주의 시작을 월요일로 설정
        LocalDate monday = startDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        while (monday.isBefore(endDay) || monday.isEqual(endDay)) {
            // 주의 첫 날과 마지막 날 계산
            LocalDate weekStartDate = monday;
            LocalDate weekEndDate = monday.plusDays(6);

            WeeklyKcal weeklyWeight = new WeeklyKcal(weekStartDate, weekEndDate, 0, 0, 0, 0);
            weeklyWeights.add(weeklyWeight);

            // 다음 주의 월요일로 이동
            monday = monday.plusWeeks(1);
        }

        return weeklyWeights;
    }

    private void updateWeeklyKcalResponses(List<WeeklyKcal> result, List<WeeklyKcalNativeVo> kcals) {
        kcals.forEach(week -> {
            int index = (int) ChronoUnit.WEEKS.between(result.get(0).getWeekStartDate(), LocalDate.parse(week.getWeekStartDate()));
            if (index >= 0 && index < result.size()) {
                String mealIds = week.getMealIds();
                String[] mealIdsArray = mealIds.split(","); // 쉼표로 구분된 문자열을 배열로 분할

                List<Meal> meals = new ArrayList<>();
                for (String mealIdStr : mealIdsArray) {
                    Long mealId = Long.parseLong(mealIdStr.trim()); // 문자열을 Long으로 변환
                    Meal meal = mealRepository.findByMealIdAndStatus(mealId, BaseStatus.A)
                            .orElseThrow(()-> new MealException(BaseExceptionResponseStatus.MEAL_NOT_FOUND));
                    meals.add(meal);
                }
                int kcal = (int) (memberService.calculateIntakeKcal(meals) / week.getDaysInWeek());
                int[] nutrient = memberService.calculateNutrient(meals);
                result.set(index, new WeeklyKcal(LocalDate.parse(week.getWeekStartDate()), LocalDate.parse(week.getWeekEndDate()), kcal,
                        (int) (nutrient[0]/week.getDaysInWeek()), (int) (nutrient[1]/week.getDaysInWeek()), (int) (nutrient[2]/week.getDaysInWeek())));
            }
        });
    }
}
