package mealplanb.server.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchPlanRequest {
    /** 사용자 목표 수정 */

    @NotNull(message = "initialWeight: {NotNull}")
    private double initialWeight;

    @NotNull(message = "targetWeight: {NotNull}")
    private double targetWeight;

    @NotNull(message = "dietType: {NotNull}")
    private String dietType;

    @NotNull(message = "carbohydrateRate: {NotNull}")
    private int carbohydrateRate;

    @NotNull(message = "proteinRate: {NotNull}")
    private int proteinRate;

    @NotNull(message = "fatRate: {NotNull}")
    private int fatRate;

    @NotNull(message = "targetKcal: {NotNull}")
    private int targetKcal;
}
