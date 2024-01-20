package mealplanb.server.repository;

import mealplanb.server.domain.FoodFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodFavoriteRepository extends JpaRepository<FoodFavorite, Long> {
}
