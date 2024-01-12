package mealplanb.server.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchUserPlanResponse {

    private double initial_weight;
    private double target_weight;
    private String diet_type;
    private int carbohydrate_rate;
    private int protein_rate;
    private int fat_rate;
    private int target_kcal;
}
