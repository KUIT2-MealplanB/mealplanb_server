package mealplanb.server.domain.Member;

import jakarta.persistence.*;
import lombok.Getter;
import mealplanb.server.domain.BaseTimeEntity;
import mealplanb.server.domain.Meal;
import mealplanb.server.domain.Weight;

import java.time.LocalDate;
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
    private int nickname;
    private int avatarColor;
    private int skeletalMuscleMass;
    private int bodyFatMass;
    private LocalDate targetUpdatedAt;

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
