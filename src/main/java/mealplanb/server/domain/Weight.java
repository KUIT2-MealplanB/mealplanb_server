package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Base.BaseTimeEntity;
import mealplanb.server.domain.Member.Member;

@Entity
@Getter
@Table(name = "Weight")
public class Weight extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weight_id", updatable = false)
    private Long weightId;

    private double weight;

    // Member : 일대다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private BaseStatus status;
}
