package mealplanb.server.repository;

import mealplanb.server.domain.Food;
import mealplanb.server.dto.food.GetFavoriteFoodResponse;
import mealplanb.server.dto.food.GetFoodAutoCompleteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {

    Optional<Food> findByFoodId(Long foodId);

    @Query(value = "SELECT * " +
            "FROM food " +
            "WHERE name LIKE %:query% " +
            "ORDER BY (SELECT COUNT(food_id) FROM food_meal_mapping_table WHERE food_id = food.food_id) DESC", nativeQuery = true)
    Page<Food> getAutoComplete(@Param("query") String query, Pageable pageable);
}
