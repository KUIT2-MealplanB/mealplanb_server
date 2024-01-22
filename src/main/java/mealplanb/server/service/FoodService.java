package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Food;
import mealplanb.server.dto.food.GetFoodResponse;
import mealplanb.server.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final FoodFavoriteService foodFavoriteService;

    public GetFoodResponse getFoodDetail(long memberId, long foodId) {
        System.out.println("[FoodService.getFoodDetail]");

        Food food = foodRepository.findById(foodId)
                .orElseThrow(()-> new FoodException(BaseExceptionResponseStatus.FOOD_NOT_FOUND));

        return new GetFoodResponse(
                food.getName(),
                food.getQuantity(),
                food.getKcal(),
                food.getCarbohydrate(),
                food.getProtein(),
                food.getFat(),
                foodFavoriteService.isFavorite(memberId, foodId)
        );
    }
}
