package mealplanb.server.domain.Member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mealplanb.server.domain.*;
import mealplanb.server.domain.Base.BaseTimeEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Member(회원 정보) 엔티티
 * */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "member")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", updatable = false)
    private Long memberId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    private String password;

    //소셜 로그인 구현 되어야함

    @Enumerated(EnumType.STRING)
    private MemberSex sex;

    private int age;
    private int height;
    private double initialWeight;
    private double targetWeight;
    private int recommendedKcal;
    private int targetKcal;
    private String dietType;
    private int carbohydrateRate;
    private int proteinRate;
    private int fatRate;
    private String nickname;
    private String avatarColor;
    private int skeletalMuscleMass;
    private int bodyFatMass;
    private LocalDate targetUpdatedAt;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    // Member 와 Weight : 일대다 관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Weight> weights = new ArrayList<>();

    // Member 와 Meal : 일대다 관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meal> meals = new ArrayList<>();

    // Member 와 FoodMealMappingTable : 일대다 관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodMealMappingTable> foodMealMappingTables = new ArrayList<>();

    // Member 와 Food_Favorite : 일대다 관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodFavorite> foodFavorites = new ArrayList<>();

    public Member(String email, String password, String sex, int age, int height, double initialWeight, double targetWeight, double dietType, double avatarColor, String nickname) {
        super();
    }
}
