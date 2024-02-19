package mealplanb.server.dto.food;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetFavoriteFoodResponse {

    /**
     * 즐겨찾기한 식품 조회 & “최근추천”을 누르면 추천받은 식사 조회
     */
    private Long foodId;
    private String foodName;
    private int kcal;
}
