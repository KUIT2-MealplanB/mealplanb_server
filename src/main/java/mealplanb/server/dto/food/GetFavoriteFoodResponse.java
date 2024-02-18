package mealplanb.server.dto.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mealplanb.server.domain.Food.Food;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetFavoriteFoodResponse {
    /** 즐겨찾기한 식품 조회 */
    private List<FoodItem> foods;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class FoodItem {
        private Long foodId;
        private String foodName;
        private int kcal;
    }

}
