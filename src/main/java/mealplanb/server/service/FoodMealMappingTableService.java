package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Food;
import mealplanb.server.domain.FoodMealMappingTable;
import mealplanb.server.domain.Meal;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.meal.MealResponse;
import mealplanb.server.dto.meal.PostMealFoodRequest;
import mealplanb.server.repository.FoodMealMappingTableRepository;
import mealplanb.server.repository.FoodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        List<FoodMealMappingTable> foodsInMeal = foodMealMappingTableRepository.findAllById(Collections.singletonList(mealId));

        double mealKcal = 0.0;
        for (FoodMealMappingTable item: foodsInMeal){
            double kcalPerGram = item.getFood().getKcal()/100;
            double foodKcal = item.getQuantity() * kcalPerGram;
            mealKcal += foodKcal;
        }
        log.info("[FoodMealMappingTableService.getMealKcal] : mealId = {}, mealKcal = {}", mealId, mealKcal);
        return mealKcal;
    }

    /**
     * 해당 끼니에 식사 리스트 등록
     */
    @Transactional
    public void postMealFood(Member member, Meal meal, List<FoodItem> foods) {
        for (FoodItem foodItem : foods){
            Food food = foodRepository.findById(foodItem.getFoodId())
                    .orElseThrow(() -> new FoodException(BaseExceptionResponseStatus.FOOD_NOT_FOUND));
            FoodMealMappingTable foodMealMappingTable = new FoodMealMappingTable(member, meal, food, foodItem.getQuantity(), false);
            foodMealMappingTableRepository.save(foodMealMappingTable);
        }
    }
}
