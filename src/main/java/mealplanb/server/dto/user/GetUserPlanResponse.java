package mealplanb.server.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetUserPlanResponse {

    private double initial_weight;
    private double target_weight;
    private int carbohydrate_rate;
    private int protein_rate;
    private int fat_rate;
    private int recommended_kcal;
    private int target_kcal;
}
