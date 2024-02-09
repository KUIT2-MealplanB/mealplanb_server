package mealplanb.server.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class GetRecommendedKcalRequest {
    /**
     * 사용자 목표 조회 (권장 칼로리 반환)
     */
    private double initialWeight;
    private double targetWeight;
}
