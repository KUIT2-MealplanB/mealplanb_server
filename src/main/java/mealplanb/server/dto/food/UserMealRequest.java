package mealplanb.server.dto.food;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserMealRequest {
    /** 끼니 등록, 끼니 수정 */
    private int mealType;
    private LocalDate mealDate;
    private List<MealItem> meals;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MealItem {
        private long foodId;
        private int quantity;
    }
}
