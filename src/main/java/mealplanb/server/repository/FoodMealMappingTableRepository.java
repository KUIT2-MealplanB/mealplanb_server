package mealplanb.server.repository;

import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FoodMealMappingTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodMealMappingTableRepository extends JpaRepository<FoodMealMappingTable, Long> {
    Optional<List<FoodMealMappingTable>> findAllByMeal_MealIdAndStatus(long mealId, BaseStatus a);
}
