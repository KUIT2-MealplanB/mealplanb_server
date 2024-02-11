package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MealException;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FavoriteMeal;
import mealplanb.server.domain.FavoriteMealComponent;
import mealplanb.server.domain.Food;
import mealplanb.server.dto.meal.GetMyMealResponse.FavoriteMealItem;
import mealplanb.server.repository.FavoriteMealComponentRepository;
import mealplanb.server.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.FAVORITE_MEAL_COMPONENT_NOT_EXIST;
import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.FAVORITE_MEAL_NOT_EXIST;


@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteMealComponentService {

    private final FoodRepository foodRepository;
    private final FavoriteMealComponentRepository favoriteMealComponentRepository;

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
                        .orElseThrow(() -> new MealException(FAVORITE_MEAL_COMPONENT_NOT_EXIST));

                // 식재료 량에 따라 칼로리 계산
                totalKcal += (int)(food.getKcal() * component.getQuantity() / 100);
            }
            FavoriteMealItem mealItem = new FavoriteMealItem(
                    favoriteMealId,
                    meal.getFavoriteMealName(),
                    totalKcal
            );

            mealItemList.add(mealItem);
        }
        return mealItemList;
    }
}
