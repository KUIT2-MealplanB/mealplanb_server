package mealplanb.server.repository;

import mealplanb.server.domain.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {

    Optional<List<Meal>> findByMember_MemberIdAndMealDate(Long memberId, LocalDate mealDate);
}
