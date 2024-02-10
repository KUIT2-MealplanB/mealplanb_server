package mealplanb.server.dto.food;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static mealplanb.server.dto.food.GetFavoriteFoodResponse.*;

@Getter
@AllArgsConstructor
public class GetFoodAutoCompleteResponse {
    /** 자동완성 검색 */
    private int page;
    private List<FoodItem> foods;
}
