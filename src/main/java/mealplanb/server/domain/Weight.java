package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Base.BaseTimeEntity;
import mealplanb.server.domain.Member.Member;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "weight")
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
}
