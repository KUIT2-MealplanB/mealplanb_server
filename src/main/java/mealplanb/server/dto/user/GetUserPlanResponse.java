package mealplanb.server.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetUserPlanResponse {

    @JsonProperty("initial_weight")
    private double initialWeight;

    @JsonProperty("target_weight")
    private double targetWeight;

    @JsonProperty("diet_type")
    private String dietType;

    @JsonProperty("recommended_kcal")
    private int recommendedKcal;

    @JsonProperty("carbohydrate_rate")
    private int carbohydrateRate;

    @JsonProperty("protein_rate")
    private int proteinRate;

    @JsonProperty("fat_rate")
    private int fatRate;

    @JsonProperty("target_kcal")
    private int targetKcal;
}
