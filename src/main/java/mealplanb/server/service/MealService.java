package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MealException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Meal;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.meal.GetMealFoodResponse;
import mealplanb.server.dto.meal.GetMealFoodResponse.FoodInfo;
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

    /**
     * 끼니 등록
     */
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

    /**
     * 끼니 기록 조회 (홈화면)
     */
    public GetMealResponse getMealList(Long memberId, LocalDate mealDate) {
        log.info("[MealService.getMealList]");
        Optional<List<Meal>> mealsOptional  = mealRepository.findByMember_MemberIdAndMealDate(memberId, mealDate);
        List<GetMealItem> mealItems = makeGetMealItems(mealsOptional);
        return new GetMealResponse(mealDate, mealItems);
    }

    private List<GetMealItem> makeGetMealItems(Optional<List<Meal>> mealsOptional) {
        log.info("[MealService.makeGetMealItems]");
        List<GetMealItem> mealItems = new ArrayList<>();

        if (!mealsOptional.isPresent()) { // 결과가 없는 경우
            log.info("[MealService.getMealList] - 만들어진 끼니 없음");
            return mealItems;
        }
        for (Meal meal : mealsOptional.get()){
            GetMealItem getMealItem = new GetMealItem(meal.getMealId(), meal.getMealType(), foodMealMappingTableService.getMealKcal(meal.getMealId()));
            mealItems.add(getMealItem);
        }
        return mealItems;
    }

    /**
     * 끼니의 식사 리스트 조회
     */
    public GetMealFoodResponse getMealFoodList(Long memberId, long mealId) {
        // 삭제 처리된 끼니일 경우,
        // FoodMealMappingTable에서 주어진 memberId, mealId에 해당하는 status가 A인 음식을 다 찾아보고 없다고 빈 값을 반환하는 것보다
        // Meal에서 삭제된 끼니면 아예 FoodMealMappingTable을 탐색하지 않도록 했다.
        log.info("[MealService.getMealFoodList]");
        Meal meal = mealRepository.findByMealIdAndMember_MemberIdAndStatus(mealId, memberId, BaseStatus.A)
                .orElseThrow(()->new MealException(BaseExceptionResponseStatus.MEAL_NOT_FOUND));

        List<FoodInfo> foodList = foodMealMappingTableService.getMealFoodList(mealId);
        return new GetMealFoodResponse(meal.getMealId(), meal.getMealDate(), meal.getMealType(), foodList);
    }
}

