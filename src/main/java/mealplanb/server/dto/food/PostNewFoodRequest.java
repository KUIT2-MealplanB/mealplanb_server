package mealplanb.server.dto.food;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostNewFoodRequest {
    /** 사용자로부터 입력받은 식사 데이터베이스에 등록 */

    private String name;
    private int quantity;
    private double kcal;
    private double carbohydrate;
    private double protein;
    private double fat;
    private double sugar;
    private double sodium;
    private double cholesterol;
    private double saturatedFattyAcid;
    private double transFatAcid;
}
