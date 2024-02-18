package mealplanb.server.dto.meal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostMyMealRequest {
    /**
     * 나의 식단 등록
     */
    private String favoriteMealName;
    private List<FoodItem> foods;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class FoodItem{
        private long foodId;
        private int quantity;
    }
}
