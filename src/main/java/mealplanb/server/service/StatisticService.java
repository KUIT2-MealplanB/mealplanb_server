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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                .ifPresent(weights -> updateDailyKcalResponses(result, weights));

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
}
