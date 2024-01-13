package mealplanb.server.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GetProfileMealResponse {

    /** 홈화면 식사 조회 */
    private List<Meal> meals;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Meal{

        @JsonProperty("meal_id")
        private long mealId;

        @JsonProperty("meal_type")
        private String mealType;

        @JsonProperty("total_kcal")
        private int totalKcal;
    }

}
