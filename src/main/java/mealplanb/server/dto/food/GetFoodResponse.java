package mealplanb.server.dto.food;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetFoodResponse {
    /** 특정 식사의 상세 정보 반환 */
    private Long foodId;
    private String name;
    private int quantity;
    private double kcal;
    private double carbohydrate;
    private double protein;
    private double fat;
    private boolean isFavorite;
}
