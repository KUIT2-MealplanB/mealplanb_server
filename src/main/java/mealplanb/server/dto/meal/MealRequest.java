package mealplanb.server.dto.meal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MealRequest {
    /** 끼니에 식사 등록 */
    private Long mealId;
    private List<MealItem> meals;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MealItem {
        private long foodId;
        private int quantity;
    }
}
