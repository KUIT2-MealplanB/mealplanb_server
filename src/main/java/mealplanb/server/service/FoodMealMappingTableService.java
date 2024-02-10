package mealplanb.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Food;
import mealplanb.server.domain.FoodMealMappingTable;
import mealplanb.server.domain.Meal.Meal;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.repository.FoodMealMappingTableRepository;
import mealplanb.server.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static mealplanb.server.dto.meal.PostMealFoodRequest.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodMealMappingTableService {
    private final FoodMealMappingTableRepository foodMealMappingTableRepository;
    private final FoodRepository foodRepository;

    /**
     * 해당 끼니의 총 칼로리 반환
     */
    public double getMealKcal(Long mealId) {
        log.info("[FoodMealMappingTableService.getMealKcal]");
        List<FoodMealMappingTable> foodsInMeal = foodMealMappingTableRepository.findAllByMeal_MealIdAndStatus(mealId, BaseStatus.A);

        double mealKcal = 0.0;
        for (FoodMealMappingTable item: foodsInMeal){
            log.info("[FoodMealMappingTableService.getMealKcal] : mealId = {}, foodId = {}, foodKcalPer100 = {}, foodQauntity = {}", mealId, item.getFood().getFoodId(), item.getFood().getKcal(), item.getQuantity());
            double kcalPerGram = item.getFood().getKcal()/100;
            double foodKcal = item.getQuantity() * kcalPerGram;
            mealKcal += foodKcal;
        }
        log.info("[FoodMealMappingTableService.getMealKcal] : mealId = {}, mealKcal = {}", mealId, mealKcal);
        return mealKcal;
    }

    /**
     * 끼니 삭제 시, 해당 끼니에 연결되어있는 식품 정보의 status -> D로 변경
     */
    @Transactional
    public void deleteFoodMealMapping(Long mealId){
        log.info("[FoodMealMappingTableService.deleteFoodMealMapping]");
        List<FoodMealMappingTable> foodsInMeal = foodMealMappingTableRepository.findAllByMeal_MealIdAndStatus(mealId, BaseStatus.A);
        for (FoodMealMappingTable item: foodsInMeal){
            item.setStatus(BaseStatus.D);
        }
    }

    /**
     * 해당 끼니에 식사 리스트 등록, 수정
     */
    @Transactional
    public void postMealFood(Member member, Meal meal, List<FoodItem> foods) {
        deleteFoodMealMapping(meal.getMealId());

        for (FoodItem foodItem : foods){
            Food food = foodRepository.findById(foodItem.getFoodId())
                    .orElseThrow(() -> new FoodException(BaseExceptionResponseStatus.FOOD_NOT_FOUND));
            FoodMealMappingTable foodMealMappingTable = new FoodMealMappingTable(member, meal, food, foodItem.getQuantity(), false);
            foodMealMappingTableRepository.save(foodMealMappingTable);
        }
    }

}
