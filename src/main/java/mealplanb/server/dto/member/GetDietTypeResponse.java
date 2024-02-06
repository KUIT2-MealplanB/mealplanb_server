package mealplanb.server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetDietTypeResponse {

    /**
     * 사용자 목표 조회 (식단타입에 따른 탄단지 조회)
     */
    private String dietType;
    private int carbohydrateRate;
    private int proteinRate;
    private int fatRate;
}