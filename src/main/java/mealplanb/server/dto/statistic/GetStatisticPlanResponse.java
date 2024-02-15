package mealplanb.server.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetStatisticPlanResponse {
    /** 통계페이지 상단 목표 조회 */
    private double initialWeight;
    private double targetWeight;
    private String dietType;
}

