package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import mealplanb.server.domain.Member.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "Meal")
public class Meal extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id", updatable = false)
    private Long mealId;

    //Member : member_id (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String mealName;
    private int mealType;
    private LocalDate mealDate;
    private boolean isFavorite;

    @Enumerated(EnumType.STRING)
    private BaseStatus status;

    // Meal과 FoodMealMappingTable : 일대다 관계
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodMealMappingTable> foodMealMappingTables = new ArrayList<>();
}
