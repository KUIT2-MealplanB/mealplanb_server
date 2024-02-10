package mealplanb.server.dto.meal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetMealFoodResponse {
    /** 끼니의 식사 리스트 조회 response */
    private long mealId;
    private LocalDate mealDate;
    private int mealType;
    private List<FoodInfo> foodList;

    @Getter
    @AllArgsConstructor
    public static class FoodInfo {
        private long foodId;
        private int quantity;
        private String name;
        private int kcal;
    }
}
