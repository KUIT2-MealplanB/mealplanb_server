package mealplanb.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Food;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.FoodMealMappingTable;
import mealplanb.server.dto.meal.GetMealFoodResponse.FoodInfo;
import mealplanb.server.domain.Meal.Meal;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.repository.FoodMealMappingTableRepository;
import mealplanb.server.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ArrayList;
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
        Optional<List<FoodMealMappingTable>> foodsInMeal = foodMealMappingTableRepository.findAllByMeal_MealIdAndStatus(mealId, BaseStatus.A);

        double mealKcal = 0.0;
        if (foodsInMeal.isPresent()){
            for (FoodMealMappingTable item: foodsInMeal.get()){
                log.info("[FoodMealMappingTableService.getMealKcal] : mealId = {}, foodId = {}, foodKcalPer100 = {}, foodQauntity = {}", mealId, item.getFood().getFoodId(), item.getFood().getKcal(), item.getQuantity());
                double foodKcal = calculateFoodKcal(item.getQuantity(), item.getFood().getKcal());
                mealKcal += foodKcal;
            }
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
        Optional<List<FoodMealMappingTable>> optionalFoodsInMeal = foodMealMappingTableRepository.findAllByMeal_MealIdAndStatus(mealId, BaseStatus.A);
        optionalFoodsInMeal.ifPresent(foodsInMeal -> {
            for (FoodMealMappingTable item: foodsInMeal){
                item.setStatus(BaseStatus.D);
            }
        });
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

    private int calculateFoodKcal(int quantity, double kcalPer100){
        double kcalPerGram = kcalPer100/100;
        return (int) (quantity * kcalPerGram);
    }

}
