package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Base.BaseTimeEntity;
import mealplanb.server.dto.food.PostNewFoodRequest;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "food")
public class Food extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id", updatable = false)
    private Long foodId;

    private String name;
    private String category;
    private String keyNutrient;
    @Transient
    private int quantity = 100; // 영양성분 기준양, 데이터베이스에 매핑되지 않음
    private double kcal;
    private double carbohydrate;
    private double protein;
    private double fat;
    private double sugar;
    private double sodium; // 나트륨
    private double cholesterol;
    private double saturatedFattyAcid;
    private double transFatAcid;
    private boolean isMemberCreated;

    @Enumerated(EnumType.STRING)
    private BaseStatus status;

    // Food 와 FoodMealMappingTable : 일대다 관계
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodMealMappingTable> foodMealMappingTables = new ArrayList<>();

    // Food 와 FavoriteFood : 일대다 관계
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteFood> favoriteFoods = new ArrayList<>();


    public Food(PostNewFoodRequest postNewFoodRequest) {
        super();
        this.name = postNewFoodRequest.getName();
        this.category = "unknown"; // 일단은 사용자가 카테고리를 등록하지 못하게 구현
        this.kcal = postNewFoodRequest.getKcal();
        this.carbohydrate = postNewFoodRequest.getCarbohydrate();
        this.protein = postNewFoodRequest.getProtein();
        this.fat = postNewFoodRequest.getFat();
        this.keyNutrient = findKeyNutrient(); // 순서 주의
        this.sugar = postNewFoodRequest.getSugar();
        this.sodium = postNewFoodRequest.getSodium();
        this.cholesterol = postNewFoodRequest.getCholesterol();
        this.saturatedFattyAcid = postNewFoodRequest.getSaturatedFattyAcid();
        this.transFatAcid = postNewFoodRequest.getTransFatAcid();
        this.status = BaseStatus.A;
        this.isMemberCreated = true;
    }

    private String findKeyNutrient() {
        // 주의 : carbohydrate, protein, fat 값이 다 정해진 뒤에 수행되어야한다.
        if (carbohydrate >= protein && carbohydrate >= fat) {
            return "탄수화물";
        } else if (protein >= carbohydrate && protein >= fat) {
            return "단백질";
        } else {
            return "지방";
        }
    }
}
