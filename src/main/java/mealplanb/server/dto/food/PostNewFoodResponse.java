package mealplanb.server.dto.food;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostNewFoodResponse {
    /** 사용자로부터 입력받은 식사 데이터베이스에 등록 */

    private long foodId;
    private String name;
    private String category;
    private String keyNutrient;
    private int quantity;
    private double kcal;
    private double carbohydrate;
    private double protein;
    private double fat;
    private double sugar;
    private double natrium;
    private double cholesterol;
    private double saturatedFattyAcid;
    private double transFatAcid;
}
