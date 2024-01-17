package mealplanb.server.domain.Member;

import jakarta.persistence.*;
import lombok.Getter;
import mealplanb.server.domain.BaseTimeEntity;
import mealplanb.server.domain.Weight;

import java.util.ArrayList;
import java.util.List;

/**
 * Member(회원 정보) 엔티티
 * */
@Entity
@Getter
@Table(name = "Member")
public class Member extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", updatable = false)
    private Long memberId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    //소셜 로그인 구현 되어야함

    @Column(name = "sex")
    @Enumerated(EnumType.STRING)
    private MemberSex sex;

    @Column(name = "age")
    private int age;

    @Column(name = "height")
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

    @Column(name = "nickname")
    private int nickname;

    @Column(name = "avatar_color")
    private int avatarColor;

    @Column(name = "skeletal_muscle_mass")
    private int skeletalMuscleMass;

    @Column(name = "body_fat_mass")
    private int bodyFatMass;

    @Column(name = "target_updated_at")
    private int targetUpdatedAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    // Member 와 Weight : 일대다 관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Weight> weights = new ArrayList<>();

    // Member와 Meal : 일대다 관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meal> meals = new ArrayList<>();

    //다른 테이블과의 관계 추가
}
