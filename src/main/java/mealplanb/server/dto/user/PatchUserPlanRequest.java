package mealplanb.server.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchUserPlanRequest {

    @NotNull(message = "initial_weight: {NotNull}")
    private double initial_weight;

    @NotNull(message = "target_weight: {NotNull}")
    private double target_weight;

    @NotNull(message = "diet_type: {NotNull}")
    private String diet_type;

    @NotNull(message = "carbohydrate_rate: {NotNull}")
    private int carbohydrate_rate;

    @NotNull(message = "protein_rate: {NotNull}")
    private int protein_rate;

    @NotNull(message = "fat_rate: {NotNull}")
    private int fat_rate;

    @NotNull(message = "target_kcal: {NotNull}")
    private int target_kcal;
}
