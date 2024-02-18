package mealplanb.server.dto.meal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetMyMealResponse {
    /**
     * 나의 식단 조회
     */
    private long favoriteMealId;
    private String favoriteMealName;
    private int mealKcal;
    private int foodCount; // 내용물 개수
}
