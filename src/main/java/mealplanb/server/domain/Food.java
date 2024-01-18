package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "Food")
public class Food extends BaseTimeEntity{

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

    @Enumerated(EnumType.STRING)
    private BaseStatus status;

    // Food 와 FoodMealMappingTable : 일대다 관계
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodMealMappingTable> foodMealMappingTables = new ArrayList<>();

}
