package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MealException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Meal.Meal;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.member.GetPlanResponse;
import mealplanb.server.dto.statistic.GetKcalStatisticResponse;
import mealplanb.server.dto.statistic.GetKcalStatisticResponse.DailyKcal;
import mealplanb.server.dto.statistic.GetStatisticPlanResponse;
import mealplanb.server.repository.MealRepository;
import mealplanb.server.repository.MealRepository.DailyKcalNativeVo;
import mealplanb.server.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.MEMBER_NOT_FOUND;
import static mealplanb.server.dto.statistic.GetKcalStatisticResponse.*;
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
        log.info("[StatisticService.getDailyKcals]");
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
            log.info("[StatisticService.updateDailyKcalResponses] date : {} , mealIds = {}", day.getDate(), day.getMealIds() );
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
        log.info("[StatisticService.getWeeklyKcals]");
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
                int kcal = memberService.calculateIntakeKcal(meals) / week.getDaysInWeek();
                int[] nutrient = memberService.calculateNutrient(meals);
                result.set(index, new WeeklyKcal(LocalDate.parse(week.getWeekStartDate()), LocalDate.parse(week.getWeekEndDate()), kcal,
                        nutrient[0]/week.getDaysInWeek(), nutrient[1]/week.getDaysInWeek(), nutrient[2]/week.getDaysInWeek()));
            }
        });
    }

    /**
     *  칼로리 월간 조회
     */
    public GetKcalStatisticResponse getMonthlyKcals(Long memberId) {
        log.info("[StatisticService.getMonthlyKcals]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        LocalDate startDate = member.getCreatedAt().toLocalDate();
        LocalDate endDate = LocalDate.now();

        List<MonthlyKcal> result = createMonthlyEmptyKcalResponses(startDate, endDate);
        mealRepository.findMonthlyKcal(memberId)
                .ifPresent(kcals -> updateMonthlyKcalResponses(result, kcals));

        return new GetKcalStatisticResponse("monthly", result);
    }

    private List<MonthlyKcal> createMonthlyEmptyKcalResponses(LocalDate startDate, LocalDate endDate) {
        List<MonthlyKcal> monthlyKcals = new ArrayList<>();

        // startDate의 년도와 월 정보 가져오기
        YearMonth currentMonth = YearMonth.from(startDate);

        while (!currentMonth.isAfter(YearMonth.from(endDate))) {

            MonthlyKcal monthlyKcal = new MonthlyKcal(currentMonth, 0, 0, 0, 0);
            monthlyKcals.add(monthlyKcal);

            // 다음 달로 이동
            currentMonth = currentMonth.plusMonths(1);
        }

        return monthlyKcals;
    }

    private void updateMonthlyKcalResponses(List<MonthlyKcal> result, List<MonthlyKcalNativeVo> kcals) {
        kcals.forEach(month -> {
            int index = (int) ChronoUnit.MONTHS.between(result.get(0).getMonth(), YearMonth.parse(month.getMonth()));
            if (index >= 0 && index < result.size()) {
                String mealIds = month.getMealIds();
                String[] mealIdsArray = mealIds.split(","); // 쉼표로 구분된 문자열을 배열로 분할

                List<Meal> meals = new ArrayList<>();
                for (String mealIdStr : mealIdsArray) {
                    Long mealId = Long.parseLong(mealIdStr.trim()); // 문자열을 Long으로 변환
                    Meal meal = mealRepository.findByMealIdAndStatus(mealId, BaseStatus.A)
                            .orElseThrow(()-> new MealException(BaseExceptionResponseStatus.MEAL_NOT_FOUND));
                    meals.add(meal);
                }
                int kcal = memberService.calculateIntakeKcal(meals) / month.getDaysInMonth();
                int[] nutrient = memberService.calculateNutrient(meals);
                result.set(index, new MonthlyKcal(YearMonth.parse(month.getMonth()), kcal,
                        nutrient[0]/month.getDaysInMonth(), nutrient[1]/month.getDaysInMonth(), nutrient[2]/month.getDaysInMonth()));
            }
        });
    }

    /**
     *  통계페이지 상단 목표 조회
     */
    public GetStatisticPlanResponse getStatisticPlan(Long memberId) {
        log.info("[StatisticService.getStatisticPlan]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        double initialWeight = member.getInitialWeight();
        double targetWeight = member.getTargetWeight();
        String dietType = member.getDietType();

        return new GetStatisticPlanResponse(initialWeight, targetWeight, dietType);
    }
}
