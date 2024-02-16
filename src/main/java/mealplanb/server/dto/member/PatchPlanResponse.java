package mealplanb.server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchPlanResponse {
    /** 사용자 목표 수정 */

    private double initialWeight;
    private double targetWeight;
    private int recommendedKcal;
    private String dietType;
    private int carbohydrateRate;
    private int proteinRate;
    private int fatRate;
    private int targetKcal;
}