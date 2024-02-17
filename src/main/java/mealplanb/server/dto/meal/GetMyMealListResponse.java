package mealplanb.server.dto.meal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetMyMealListResponse {
    /**
     * 나의 식단 선택해서 식사 리스트 조회하기
     */
    private long foodId;
    private String foodName;
    private int quantity;
    private int kcal;

}
