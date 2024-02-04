package mealplanb.server.domain.Meal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Base.BaseTimeEntity;
import mealplanb.server.domain.FoodMealMappingTable;
import mealplanb.server.domain.Member.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "meal")
public class Meal extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id", updatable = false)
    private Long mealId;

    //Member : member_id (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int mealType;
    private LocalDate mealDate;

    @Enumerated(EnumType.STRING)
    private BaseStatus status;

    // Meal과 FoodMealMappingTable : 일대다 관계
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodMealMappingTable> foodMealMappingTables = new ArrayList<>();


    public Meal(Member member, LocalDate mealDate, int mealType) {
        this.member = member;
        this.mealDate = mealDate;
        this.mealType = mealType;
        this.status = BaseStatus.A;
    }
}
