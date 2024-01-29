package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
