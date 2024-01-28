package mealplanb.server.dto.meal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class MealResponse {
    /** 끼니 등록, 조회, 수정의 response */
    private long mealId;
    private LocalDate mealDate;
    private int mealType;
    private List<MealItemDetail> meals;
    private double totalKcal;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MealItemDetail {
        private long foodId;
        private int quantity;
        private String name;
        private int kcal;
    }
}
