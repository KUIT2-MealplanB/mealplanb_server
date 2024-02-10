package mealplanb.server.repository;

import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FoodMealMappingTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodMealMappingTableRepository extends JpaRepository<FoodMealMappingTable, Long> {
    List<FoodMealMappingTable> findAllByMeal_MealIdAndStatus(Long mealId, BaseStatus a);
}
