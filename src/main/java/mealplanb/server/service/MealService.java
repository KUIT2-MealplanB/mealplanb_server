package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MealException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Meal.Meal;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.meal.*;
import mealplanb.server.dto.meal.GetMealResponse.GetMealItem;
import mealplanb.server.dto.meal.MealTypeConverter;
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

        // 이미 존재하는 Meal에 대한 요청이 들어왔을 때 예외처리
        checkDuplicate(mealRequest, member);

        //Meal 테이블에 저장
        Meal meal = new Meal(member, mealRequest.getMealDate(), mealRequest.getMealType());
        Meal createdMeal = mealRepository.save(meal);
        return new PostMealResponse(createdMeal.getMealId());
    }

    private void checkDuplicate(PostMealRequest mealRequest, Member member) {
        boolean exists = mealRepository.existsByMemberAndMealDateAndMealType(member, mealRequest.getMealDate(), mealRequest.getMealType());
        if (exists){
            throw new MealException(BaseExceptionResponseStatus.DUPLICATE_MEAL);
        }
    }

    public GetMealResponse getMealList(Long memberId, LocalDate mealDate) {
        log.info("[MealService.getMealList]");
        Optional<List<Meal>> mealsOptional  = mealRepository.findByMember_MemberIdAndMealDateAndStatus(memberId, mealDate, BaseStatus.A);

        if (mealsOptional.isEmpty()) { // 결과가 없는 경우
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
            GetMealItem getMealItem = new GetMealItem(meal.getMealId() ,MealTypeConverter.convertMealTypeLabel(meal.getMealType()), foodMealMappingTableService.getMealKcal(meal.getMealId()));
            mealItems.add(getMealItem);
        }
        return mealItems;
    }

    public void postMealFood(Long memberId, PostMealFoodRequest postMealFoodRequest) {
        log.info("[MealService.postMealFood]");

        //해당 mealId를 갖는 status가 A인 Meal이 존재하는 지
        Meal meal = mealRepository.findByMealIdAndStatus(postMealFoodRequest.getMealId(), BaseStatus.A)
                .orElseThrow(()-> new MealException(BaseExceptionResponseStatus.MEAL_NOT_FOUND));

        //해당 Meal의 memberId가 요청을 보낸 멤버의 아이디와 동일한지
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));
        if (!meal.getMember().equals(member)) {
            throw new MealException(BaseExceptionResponseStatus.UNAUTHORIZED_ACCESS);
        }

        foodMealMappingTableService.postMealFood(member, meal, postMealFoodRequest.getFoods());
    }
}

