package mealplanb.server.repository;

import mealplanb.server.domain.Member.Member;
import mealplanb.server.domain.Weight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeightRepository extends JpaRepository<Weight, Long> {

    Optional<Weight> findTopByMemberOrderByWeightDateDesc(Member member);
}
