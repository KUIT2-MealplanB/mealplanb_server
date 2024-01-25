package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Base.BaseTimeEntity;
import mealplanb.server.domain.Member.Member;

@Entity
@Getter
@Table(name = "FoodMealMappingTable")
public class FoodMealMappingTable extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_meal_id", updatable = false)
    private Long foodMealId;

    //Member : member_id(FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //Meal : meal_id(FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    //Food : food_if(FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    private int quantity;
    private boolean isRecommended;

    @Enumerated(EnumType.STRING)
    private BaseStatus status;
}
