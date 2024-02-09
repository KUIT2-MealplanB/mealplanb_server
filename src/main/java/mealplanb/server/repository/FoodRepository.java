package mealplanb.server.repository;

import mealplanb.server.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {

    Optional<Food> findByFoodId(Long foodId);
}
