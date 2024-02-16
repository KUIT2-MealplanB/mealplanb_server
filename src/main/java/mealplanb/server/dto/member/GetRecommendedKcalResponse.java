package mealplanb.server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetRecommendedKcalResponse {
    /** 사용자 목표 조회 (권장 칼로리 반환) */
    private int recommendedKcal;
}
