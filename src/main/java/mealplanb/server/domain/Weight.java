package mealplanb.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import mealplanb.server.domain.BaseStatus;
import mealplanb.server.domain.BaseTimeEntity;
import mealplanb.server.domain.Member.Member;

@Entity
@Getter
@Table(name = "Weight")
public class Weight extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weight_id", updatable = false)
    private Long weightId;

    // Member : 일대다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BaseStatus status;
}
