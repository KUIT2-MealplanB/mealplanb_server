package mealplanb.server.dto.meal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetMealResponse {
    private LocalDate mealDate;
    private List<GetMealItem> meals;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMealItem {
        private long mealId;
        private int mealType;
        private String mealTypeLabel;
        private double mealKcal;
    }

    public GetMealResponse(LocalDate mealDate) {
        this.mealDate = mealDate;
        this.meals = new ArrayList<>();
    }

}
