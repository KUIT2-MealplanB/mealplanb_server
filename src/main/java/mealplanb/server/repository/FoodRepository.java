package mealplanb.server.repository;

import mealplanb.server.domain.Food;
import mealplanb.server.dto.food.GetFavoriteFoodResponse;
import mealplanb.server.dto.food.GetFoodAutoCompleteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {

    Optional<Food> findByFoodId(Long foodId);

    @Query(value = "SELECT * " +
            "FROM food " +
            "WHERE name LIKE %:query%" , nativeQuery = true)
    Page<Food> getAutoComplete(@Param("query") String query, Pageable pageable);
}
