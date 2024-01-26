package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mealplanb.server.domain.Base.BaseTimeEntity;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "favorite_meal_component")
public class FavoriteMealComponent extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_meal_component_id", updatable = false)
    private Long favoriteMealComponentId;

    // FavoriteMeal : favorite_meal_id(FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_meal_id")
    private FavoriteMeal favoriteMeal;

    //Food : food_if(FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

}