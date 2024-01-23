package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Base.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "Food")
public class Food extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id", updatable = false)
    private Long foodId;

    private String name;
    private String category;
    private String keyNutrient;
    private double kcal;
    private double carbohydrate;
    private double protein;
    private double fat;
    private double sugar;
    private double sodium; // 나트륨
    private double cholesterol;
    private double saturatedFattyAcid;
    private double transFatAcid;
    @Transient
    private int quantity = 100; // 영양성분 기준양, 데이터베이스에 매핑되지 않음

    @Enumerated(EnumType.STRING)
    private BaseStatus status;

    // Food 와 FoodMealMappingTable : 일대다 관계
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodMealMappingTable> foodMealMappingTables = new ArrayList<>();

    // Food 와 FoodFavorite : 일대다 관계
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodFavorite> foodFavorites = new ArrayList<>();
}
