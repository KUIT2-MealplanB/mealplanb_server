package mealplanb.server.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class PatchPlanResponse {

    private double initialWeight;
    private double targetWeight;
    private String dietType;
    private int carbohydrateRate;
    private int proteinRate;
    private int fatRate;
    private int targetKcal;
}