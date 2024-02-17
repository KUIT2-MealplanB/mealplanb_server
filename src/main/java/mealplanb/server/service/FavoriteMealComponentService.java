package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.exception.MealException;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FavoriteMeal;
import mealplanb.server.domain.FavoriteMealComponent;
import mealplanb.server.domain.Food.Food;
import mealplanb.server.dto.meal.GetMyMealListResponse;
import mealplanb.server.dto.meal.GetMyMealResponse.FavoriteMealItem;
import mealplanb.server.dto.meal.PostMyMealRequest;
import mealplanb.server.repository.FavoriteMealComponentRepository;
import mealplanb.server.repository.FoodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.*;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteMealComponentService {

    private final FoodRepository foodRepository;
    private final FavoriteMealComponentRepository favoriteMealComponentRepository;

    /**
     * 나의 식단 등록
     */
    @Transactional
    public void saveItems(PostMyMealRequest postMyMealRequest,FavoriteMeal favoriteMeal){
        for (PostMyMealRequest.FoodItem foodItem : postMyMealRequest.getFoods()) {
            long foodId = foodItem.getFoodId();
            int quantity = foodItem.getQuantity();
            Food food = foodRepository.findByFoodId(foodId)
                    .orElseThrow(() -> new FoodException(FOOD_NOT_FOUND));

            FavoriteMealComponent favoriteMealComponent = FavoriteMealComponent.builder()
                    .favoriteMeal(favoriteMeal)
                    .food(food)
                    .quantity(quantity)
                    .status(BaseStatus.A)
                    .build();

            favoriteMealComponentRepository.save(favoriteMealComponent);
        }
    }


    /**
     * 나의 식단 조회
     */
    public List<FavoriteMealItem> getFavoriteMealComponentList(List<FavoriteMeal> favoriteMeal){
        log.info("[FavoriteMealComponentService.getFavoriteMealComponentList]");
        List<FavoriteMealItem> mealItemList = new ArrayList<>();

        int totalKcal = 0;
        for(FavoriteMeal meal : favoriteMeal){
            Long favoriteMealId = meal.getFavoriteMealId();
            List<FavoriteMealComponent> favoriteMealComponentList = favoriteMealComponentRepository.findByFavoriteMeal_FavoriteMealIdAndStatus(favoriteMealId, BaseStatus.A)
                    .orElseThrow(()-> new MealException(FAVORITE_MEAL_NOT_EXIST)); //해당 유저의 나의 식단이 존재하지 않습니다.

            for (FavoriteMealComponent component : favoriteMealComponentList) {
                Food food = foodRepository.findByFoodId(component.getFood().getFoodId())
                        .orElseThrow(() -> new MealException(FAVORITE_MEAL_COMPONENT_NOT_EXIST)); // 나의 식단에 들어있는 식사가 없습니다.

                // 식재료 량에 따라 칼로리 계산
                totalKcal += (int)(food.getKcal() * component.getQuantity() / 100);
            }
            FavoriteMealItem mealItem = new FavoriteMealItem(
                    favoriteMealId,
                    meal.getFavoriteMealName(),
                    totalKcal,
                    favoriteMealComponentList.size()
            );

            mealItemList.add(mealItem);
        }
        return mealItemList;
    }

    /**
     * 나의 식단 삭제
     */
    @Transactional
    public void deleteMyMealComponent(Long favoriteMealId){
        log.info("[FavoriteMealComponentService.deleteMyMealComponent]");
        List<FavoriteMealComponent> favoriteMealComponentList = favoriteMealComponentRepository.findByFavoriteMeal_FavoriteMealIdAndStatus(favoriteMealId,BaseStatus.A)
                .orElseThrow(()->new MealException(FAVORITE_MEAL_COMPONENT_NOT_EXIST));

        for(FavoriteMealComponent component : favoriteMealComponentList){
            component.updateStatus(BaseStatus.D);
        }
    }

    /**
     * 나의 식단 선택해서 식사 리스트 조회하기
     */
    public List<GetMyMealListResponse> getMyMealList(Long favoriteMealId){
        log.info("[FavoriteMealComponentService.getMyMealList]");
        List<GetMyMealListResponse> myMealItemList = new ArrayList<>();

        List<FavoriteMealComponent> favoriteMealComponentList = favoriteMealComponentRepository.findByFavoriteMeal_FavoriteMealIdAndStatus(favoriteMealId,BaseStatus.A)
                .orElseThrow(()->new MealException(FAVORITE_MEAL_COMPONENT_NOT_EXIST));

        for(FavoriteMealComponent component : favoriteMealComponentList){
            int kcal = 0;
            long foodId = component.getFood().getFoodId();
            Food food = foodRepository.findByFoodId(foodId)
                    .orElseThrow(() -> new MealException(FOOD_NOT_FOUND));

            // 식재료 량에 따라 칼로리 계산
            kcal = (int)(food.getKcal() * component.getQuantity() / 100);

            GetMyMealListResponse getMyMealItem= new GetMyMealListResponse(
                    foodId,
                    food.getName(),
                    component.getQuantity(),
                    kcal
            );
            myMealItemList.add(getMyMealItem);
        }
        return myMealItemList;
    }

}
