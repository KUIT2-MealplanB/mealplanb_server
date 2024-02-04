package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Base.BaseTimeEntity;
import mealplanb.server.domain.Member.Member;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "weight")
@NoArgsConstructor
public class Weight extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weight_id", updatable = false)
    private Long weightId;

    // Member : 일대다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private double weight;
    private LocalDate weightDate;

    @Enumerated(EnumType.STRING)
    private BaseStatus status;

    @Builder
    public Weight(Member member, double weight, LocalDate weightDate, BaseStatus status) {
        this.member = member;
        this.weight = weight;
        this.weightDate = weightDate;
        this.status = status;
    }
}
