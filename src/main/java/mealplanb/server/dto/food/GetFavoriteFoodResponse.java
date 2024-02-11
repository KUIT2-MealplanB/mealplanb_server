package mealplanb.server.dto.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetFavoriteFoodResponse {
    /**
     * 즐겨찾기한 식품 조회
     */
    private List<FoodItem> foods;

    @Getter
    @Setter
    public static class FoodItem {
        private Long foodId;
        private String foodName;
        private int kcal;

        public FoodItem(Long foodId, String name, int kcal) {
            this.foodId = foodId;
            this.foodName = name;
            this.kcal = kcal;
        }
    }

}