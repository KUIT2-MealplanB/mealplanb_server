package mealplanb.server.dto.user;

import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchUserPlanRequest {

    @NotNull(message = "initialWeight: {NotNull}")
    @JsonProperty("initial_weight")
    private double initialWeight;

    @NotNull(message = "targetWeight: {NotNull}")
    @JsonProperty("target_weight")
    private double targetWeight;

    @NotNull(message = "dietType: {NotNull}")
    @JsonProperty("diet_type")
    private String dietType;

    @NotNull(message = "carbohydrateRate: {NotNull}")
    @JsonProperty("carbohydrate_rate")
    private int carbohydrateRate;

    @NotNull(message = "proteinRate: {NotNull}")
    @JsonProperty("protein_rate")
    private int proteinRate;

    @NotNull(message = "fatRate: {NotNull}")
    @JsonProperty("fat_rate")
    private int fatRate;

    @NotNull(message = "targetKcal: {NotNull}")
    @JsonProperty("target_kcal")
    private int targetKcal;
}
