package mealplanb.server.repository;

import mealplanb.server.domain.FavoriteMeal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteMealRepository extends JpaRepository<FavoriteMeal, Long> {
}
