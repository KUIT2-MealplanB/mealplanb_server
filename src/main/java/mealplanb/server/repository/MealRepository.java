package mealplanb.server.repository;

import mealplanb.server.domain.Meal;
import mealplanb.server.domain.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {
    boolean existsByMemberAndMealDateAndMealType(Member member, LocalDate mealDate, int mealType);
    Optional<List<Meal>> findByMember_MemberIdAndMealDate(Long memberId, LocalDate mealDate);
    Optional<Meal> findByMealIdAndMember_MemberId(long mealId, Long memberId);
    Optional<List<Meal>> findAllByMealDateAndMemberAndMealTypeGreaterThan(LocalDate mealDate, Member member, int mealType);
}
