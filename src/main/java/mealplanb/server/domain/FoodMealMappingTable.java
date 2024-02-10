package mealplanb.server.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Base.BaseTimeEntity;
import mealplanb.server.domain.Meal.Meal;
import mealplanb.server.domain.Member.Member;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "food_meal_mapping_table")
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

    //Food : food_id(FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    private int quantity;
    private boolean isRecommended;

    @Setter
    @Enumerated(EnumType.STRING)
    private BaseStatus status;

    public FoodMealMappingTable(Member member, Meal meal, Food food, int quantity, boolean isRecommended) {
        this.member = member;
        this.meal = meal;
        this.food = food;
        this.quantity = quantity;
        this.isRecommended = isRecommended;
        this.status = BaseStatus.A;
    }

    public Food getFood() {
        return this.food;
    }
}