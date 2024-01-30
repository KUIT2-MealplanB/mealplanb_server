package mealplanb.server.repository;

import mealplanb.server.domain.Meal;
import mealplanb.server.domain.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface MealRepository extends JpaRepository<Meal, Long> {
    boolean existsByMemberAndMealDateAndMealType(Member member, LocalDate mealDate, int mealType);
}
