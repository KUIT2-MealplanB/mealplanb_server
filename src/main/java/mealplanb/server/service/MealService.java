package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Meal;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.meal.GetMealResponse;
import mealplanb.server.dto.meal.GetMealResponse.GetMealItem;
import mealplanb.server.dto.meal.PostMealRequest;
import mealplanb.server.dto.meal.PostMealResponse;
import mealplanb.server.repository.MealRepository;
import mealplanb.server.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MealService {
    private final MealRepository mealRepository;
    private final MemberRepository memberRepository;
    private final FoodMealMappingTableService foodMealMappingTableService;

    public PostMealResponse postMeal(Long memberId, PostMealRequest mealRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        //Meal 테이블에 저장
        Meal meal = new Meal(member, mealRequest.getMealDate(), mealRequest.getMealType());
        // todo: 해당 날짜, mealtype에 해당하는 meal이 이미 있는 경우에 대한 예외처리를 해야하나...?
        Meal createdMeal = mealRepository.save(meal);
        return new PostMealResponse(createdMeal.getMealId());
    }

    public GetMealResponse getMealList(Long memberId, LocalDate mealDate) {
        log.info("[MealService.getMealList]");
        Optional<List<Meal>> mealsOptional  = mealRepository.findByMember_MemberIdAndMealDate(memberId, mealDate);

        if (!mealsOptional.isPresent()) { // 결과가 없는 경우
            log.info("[MealService.getMealList] - 만들어진 끼니 없음");
            return new GetMealResponse(mealDate);
        }

        List<GetMealItem> mealItems = makeGetMealItems(mealsOptional.get());
        return new GetMealResponse(mealDate, mealItems);
    }

    private List<GetMealItem> makeGetMealItems(List<Meal> meals) {
        log.info("[MealService.makeGetMealItems]");
        List<GetMealItem> mealItems = new ArrayList<>();
        for (Meal meal : meals){
            GetMealItem getMealItem = new GetMealItem(meal.getMealId(), meal.getMealType(), foodMealMappingTableService.getMealKcal(meal.getMealId()));
            mealItems.add(getMealItem);
        }
        return mealItems;
    }
}
