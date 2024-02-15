package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.exception.MealException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FavoriteMeal;
import mealplanb.server.domain.FavoriteMealComponent;
import mealplanb.server.domain.Food.Food;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.meal.GetMyMealResponse;
import mealplanb.server.dto.meal.GetMyMealResponse.FavoriteMealItem;
import mealplanb.server.dto.meal.PostMyMealRequest;
import mealplanb.server.repository.FavoriteMealComponentRepository;
import mealplanb.server.repository.FavoriteMealRepository;
import mealplanb.server.repository.FoodRepository;
import mealplanb.server.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteMealService {

    private final MemberRepository memberRepository;
    private final FoodRepository foodRepository;
    private final FavoriteMealRepository favoriteMealRepository;
    private final FavoriteMealComponentRepository favoriteMealComponentRepository;
    private final FavoriteMealComponentService favoriteMealComponentService;

    /**
     * 나의 식단 등록
     */
    @Transactional
    public void postMyMeal(Long memberId, PostMyMealRequest postMyMealRequest){
        log.info("[FavoriteMealService.postMyMeal]");

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        String favoriteMealName = postMyMealRequest.getFavoriteMealName();
        checkFavoriteMealNameExist(favoriteMealName); // 동일한 식단 이름 있는지 검사

        FavoriteMeal favoriteMeal = FavoriteMeal.builder()
                .member(member)
                .favoriteMealName(favoriteMealName)
                .status(BaseStatus.A)
                .build();

        favoriteMealRepository.save(favoriteMeal);

        // 이 부분을 FavoriteMealComponentService 로 빼야하는지..
        for (PostMyMealRequest.FoodItem foodItem : postMyMealRequest.getFoods()) {
            long foodId = foodItem.getFoodId();
            int quantity = foodItem.getQuantity();
            Food food = foodRepository.findByFoodIdAndStatus(foodId, BaseStatus.A)
                    .orElseThrow(()-> new FoodException(FOOD_NOT_FOUND));

            FavoriteMealComponent favoriteMealComponent = FavoriteMealComponent.builder()
                    .favoriteMeal(favoriteMeal)
                    .food(food)
                    .quantity(quantity)
                    .status(BaseStatus.A)
                    .build();

            favoriteMealComponentRepository.save(favoriteMealComponent);
        }
    }

    private void checkFavoriteMealNameExist(String favoriteMealName) {
        if(favoriteMealRepository.existsByFavoriteMealNameAndStatus(favoriteMealName, BaseStatus.A)){
            throw new MealException(FAVORITE_MEAL_NAME_ALREADY_EXIST);
        }
    }

    /**
     * 나의 식단 조회
     */
    @Transactional(readOnly = true)
    public GetMyMealResponse getMyMeal(Long memberId){
        log.info("[FavoriteMealService.getMyMeal]");

        if(memberRepository.findById(memberId).isEmpty()){
            throw new MemberException(MEMBER_NOT_FOUND);
        }

        List<FavoriteMeal> favoriteMeal = favoriteMealRepository.findByMember_MemberIdAndStatus(memberId,BaseStatus.A)
                .orElseThrow(()-> new MealException(FAVORITE_MEAL_NOT_EXIST));

        List<FavoriteMealItem> favoriteMealComponentList = favoriteMealComponentService.getFavoriteMealComponentList(favoriteMeal);
        return new GetMyMealResponse(favoriteMealComponentList);
    }

    /**
     * 나의 식단 삭제
     */
    @Transactional
    public void deleteMyMeal(Long memberId, Long favoriteMealId){
        log.info("[FavoriteMealService.deleteMyMeal]");

        FavoriteMeal favoriteMeal = favoriteMealRepository.findByMember_MemberIdAndFavoriteMealIdAndStatus(memberId, favoriteMealId, BaseStatus.A)
                .orElseThrow(()->new MealException(FAVORITE_MEAL_NOT_EXIST));

        favoriteMeal.updateStatus(BaseStatus.D);
    }

}