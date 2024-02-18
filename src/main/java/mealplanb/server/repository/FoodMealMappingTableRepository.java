package mealplanb.server.repository;

import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FoodMealMappingTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodMealMappingTableRepository extends JpaRepository<FoodMealMappingTable, Long> {
    Optional<List<FoodMealMappingTable>> findAllByMeal_MealIdAndStatus(long mealId, BaseStatus a);

    @Query(value = "SELECT fm.food_id " +
            "FROM food_meal_mapping_table fm " +
            "WHERE fm.member_id = :memberId " +
            "AND fm.status = 'A' "+
            "GROUP BY fm.food_id " +
            "ORDER BY COUNT(*) DESC "+
            "LIMIT 1", nativeQuery = true)
    Optional<Long> findMostEatenFoodIdByUserId(@Param("memberId") Long memberId);
}
