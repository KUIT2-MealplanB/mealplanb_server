package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Base.BaseTimeEntity;
import mealplanb.server.domain.Member.Member;

@Entity
@Getter
@Table(name = "Food_Favorite")
public class FoodFavorite extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id", updatable = false)
    private Long favoriteId;

    // Member : member_id(FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // Food : food_id(FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    @Enumerated(EnumType.STRING)
    private BaseStatus status;
}
