package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Base.BaseTimeEntity;
import mealplanb.server.domain.Member.Member;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "favorite_meal")
public class FavoriteMeal extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_meal_id", updatable = false)
    private Long favoriteMealId;

    // Member : member_id(FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String favoriteMealName;

    @Enumerated(EnumType.STRING)
    private BaseStatus status;

}
