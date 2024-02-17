package mealplanb.server.repository;

import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FavoriteMealComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteMealComponentRepository extends JpaRepository<FavoriteMealComponent, Long> {
    Optional<List<FavoriteMealComponent>> findByFavoriteMeal_FavoriteMealIdAndStatus(Long favoriteMealId, BaseStatus A);
}