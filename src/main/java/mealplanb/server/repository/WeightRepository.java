package mealplanb.server.repository;

import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.domain.Weight;
import mealplanb.server.dto.weight.WeightResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.WildcardType;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

public interface WeightRepository extends JpaRepository<Weight, Long> {

    Optional<Weight> findTopByMemberOrderByWeightDateDesc(Member member);
    Optional<Weight> findTopByMember_MemberIdOrderByWeightDateDesc(Long memberId);
    Optional<Weight> findByMemberAndWeightDate(Member member, LocalDate date);
    Optional<List<Weight>> findAllByMemberAndStatusOrderByWeightDate(Member member, BaseStatus a);
}