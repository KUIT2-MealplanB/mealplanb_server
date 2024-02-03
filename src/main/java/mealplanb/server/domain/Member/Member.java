package mealplanb.server.domain.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mealplanb.server.domain.*;
import mealplanb.server.domain.Base.BaseTimeEntity;
import mealplanb.server.domain.Meal.Meal;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Member(회원 정보) 엔티티
 * */
@Entity
@Getter
@Setter
@ToString
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

    @Column(name = "initial_weight")
    private double initialWeight;
    @Column(name = "target_weight")
    private double targetWeight;
    @Column(name = "recommended_kcal")
    private int recommendedKcal;
    @Column(name = "target_kcal")
    private int targetKcal;
    @Column(name = "diet_type")
    private String dietType;
    @Column(name = "carbohydrate_rate")
    private int carbohydrateRate;
    @Column(name = "protein_rate")
    private int proteinRate;
    @Column(name = "fat_rate")
    private int fatRate;
    private String nickname;
    @Column(name = "avatar_color")
    private String avatarColor;
    @Column(name = "skeletal_muscle_mass")
    private int skeletalMuscleMass;
    @Column(name = "body_fat_mass")
    private int bodyFatMass;

    @CreatedDate
    @Column(name = "target_updated_at")
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

    // Member 와 Favorite_Food : 일대다 관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteFood> favoriteFoods = new ArrayList<>();

    // Member와 Favorite_Meal : 일대다 관계
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteMeal> favoriteMeals = new ArrayList<>();

    public Member(String email, String password, MemberSex sex, int age, int height, double initialWeight, double targetWeight,
                  int recommendedKcal, int targetKcal, String dietType, int carbohydrateRate, int proteinRate, int fatRate,
                  String avatarColor, String nickname, int skeletalMuscleMass, int bodyFatMass, MemberStatus status) {
        this.email = email;
        this.password = password;
        this.sex = sex;
        this.age = age;
        this.height = height;
        this.initialWeight = initialWeight;
        this.targetWeight = targetWeight;
        this.recommendedKcal = recommendedKcal;
        this.targetKcal = targetKcal;
        this.dietType = dietType;
        this.carbohydrateRate = carbohydrateRate;
        this.proteinRate = proteinRate;
        this.fatRate = fatRate;
        this.avatarColor = avatarColor;
        this.nickname = nickname;
        this.skeletalMuscleMass = skeletalMuscleMass;
        this.bodyFatMass = bodyFatMass;
        this.status = status;
        this.targetUpdatedAt = LocalDate.now();
    }

}
