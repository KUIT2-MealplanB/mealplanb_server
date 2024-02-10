package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Base.BaseTimeEntity;
import mealplanb.server.domain.Member.Member;

import java.util.ArrayList;
import java.util.List;

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

    // Favorite_Meal 과 Favorite_Meal_Component : 일대다 관계
    @OneToMany(mappedBy = "favoriteMeal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteMealComponent> favoriteMealComponents = new ArrayList<>();

    @Builder(toBuilder = true)
    public FavoriteMeal(Member member, String favoriteMealName, BaseStatus status) {
        this.member = member;
        this.favoriteMealName = favoriteMealName;
        this.status = status;
    }

}