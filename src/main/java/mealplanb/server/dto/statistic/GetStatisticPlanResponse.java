package mealplanb.server.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetStatisticPlanResponse {
    private double initialWeight;
    private double targetWeight;
    private String dietType;
}

