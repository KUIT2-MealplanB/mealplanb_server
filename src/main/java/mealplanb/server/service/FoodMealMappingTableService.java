package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Food;
import mealplanb.server.domain.FoodMealMappingTable;
import mealplanb.server.dto.meal.GetMealFoodResponse;
import mealplanb.server.dto.meal.GetMealFoodResponse.FoodInfo;
import mealplanb.server.dto.meal.GetMealResponse;
import mealplanb.server.repository.FoodMealMappingTableRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodMealMappingTableService {
    private final FoodMealMappingTableRepository foodMealMappingTableRepository;

    /**
     * 해당 끼니의 총 칼로리 반환
     */
    public double getMealKcal(Long mealId) {
        log.info("[FoodMealMappingTableService.getMealKcal]");
        List<FoodMealMappingTable> foodsInMeal = foodMealMappingTableRepository.findAllById(Collections.singletonList(mealId));

        double mealKcal = 0.0;
        for (FoodMealMappingTable item: foodsInMeal){
            double foodKcal = calculateFoodKcal(item.getQuantity(), item.getFood().getKcal());
            mealKcal += foodKcal;
        }
        log.info("[FoodMealMappingTableService.getMealKcal] : mealId = {}, mealKcal = {}", mealId, mealKcal);
        return mealKcal;
    }

    /**
     * 끼니의 식사 리스트 조회
     */
    public List<FoodInfo> getMealFoodList(long mealId) {
        log.info("[FoodMealMappingTableService.getMealFoodList]");
        Optional<List<FoodMealMappingTable>> mealFoodList= foodMealMappingTableRepository.findAllByMeal_MealIdAndStatus(mealId, BaseStatus.A);
        return makeFoodInfoList(mealFoodList);
    }

    private List<FoodInfo> makeFoodInfoList(Optional<List<FoodMealMappingTable>> foodMealMappingTables) {
        log.info("[FoodMealMappingTableService.makeFoodInfoList]");
        List<FoodInfo> foodInfoList = new ArrayList<>();

        if (foodMealMappingTables.isEmpty()) { // 식사리스트가 없는 경우
            log.info("[FoodMealMappingTableService.makeFoodInfoList] - 해당 끼니에 만들어진 식사리스트 없음");
            return foodInfoList;
        }

        // 식사리스트가 있으면
        for (FoodMealMappingTable i: foodMealMappingTables.get()){
            Food food = i.getFood();
            FoodInfo foodInfo = new FoodInfo(food.getFoodId(), i.getQuantity(), food.getName(), calculateFoodKcal(i.getQuantity(), food.getKcal()));
            foodInfoList.add(foodInfo);
        }
        return foodInfoList;
    }

    private double calculateFoodKcal(int quantity, double kcalPer100){
        double kcalPerGram = kcalPer100/100;
        return quantity * kcalPerGram;
    }
}
