package mealplanb.server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetPlanResponse {
    /** 사용자 목표 조회  */
    private double initialWeight;
    private double targetWeight;
    private String dietType;
    private int recommendedKcal;
    private int carbohydrateRate;
    private int proteinRate;
    private int fatRate;
    private int targetKcal;
}
