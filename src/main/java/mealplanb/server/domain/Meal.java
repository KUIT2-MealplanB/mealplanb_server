package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import mealplanb.server.domain.Member.Member;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "Meal")
public class Meal extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id", updatable = false)
    private Long mealId;

    //Member 테이블이랑 일대다 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String mealName;
    private int mealType;
    private LocalDate mealDate;
    private boolean isFavorite;

    @Enumerated(EnumType.STRING)
    private BaseStatus status;
}
