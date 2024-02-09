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
    /** 홈화면 (끼니 기록 조회) response */
    private LocalDate mealDate;
    private List<GetMealItem> meals;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMealItem {
        private long mealId;
        private String mealType;
        private int mealKcal;
    }

    public GetMealResponse(LocalDate mealDate) {
        this.mealDate = mealDate;
        this.meals = new ArrayList<>();
    }

}
