package mealplanb.server.dto.meal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostMealFoodRequest {
    /** 끼니에 식사 리스트 등록, 수정 */
    private Long mealId;
    private List<FoodItem> foods;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodItem {
        private long foodId;
        private int quantity;
    }
}
