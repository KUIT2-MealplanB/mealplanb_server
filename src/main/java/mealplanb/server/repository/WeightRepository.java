package mealplanb.server.repository;

import mealplanb.server.domain.Member.Member;
import mealplanb.server.domain.Weight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.WildcardType;
import java.util.Optional;

public interface WeightRepository extends JpaRepository<Weight, Long> {

    Optional<Weight> findTopByMemberOrderByWeightDateDesc(Member member);
    Optional<Weight> findTopByMember_MemberIdOrderByWeightDateDesc(Long memberId);
}
