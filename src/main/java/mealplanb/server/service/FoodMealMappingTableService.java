package mealplanb.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FoodMealMappingTable;
import mealplanb.server.repository.FoodMealMappingTableRepository;
import org.springframework.stereotype.Service;

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
        List<FoodMealMappingTable> foodsInMeal = foodMealMappingTableRepository.findAllByMeal_MealId(mealId);
        for (FoodMealMappingTable item: foodsInMeal){
            item.setStatus(BaseStatus.D);
        }
    }
}
