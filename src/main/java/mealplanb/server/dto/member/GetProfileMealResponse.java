package mealplanb.server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetProfileMealResponse {

    /** 홈화면 식사 조회 */
    private List<Meal> meals;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Meal{
        private long mealId;
        private String mealType;
        private int totalKcal;
    }

}
