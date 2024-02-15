package mealplanb.server.repository;

import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Food.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findByFoodIdAndStatus(long foodId, BaseStatus a);
    Optional<Food> findByFoodId(Long foodId);

    @Query(value = "SELECT * " +
            "FROM food " +
            "WHERE name LIKE %:query% AND status = 'A'" , nativeQuery = true)
    Page<Food> getAutoComplete(@Param("query") String query, Pageable pageable);

    @Query("SELECT f FROM Food f " +
            "WHERE f.keyNutrient = ?2 " +
            "AND f.category = ?3 " +
            "AND f.kcal BETWEEN 0 AND ?1 " +
            "ORDER BY f.kcal DESC "+
            "LIMIT 10")
    Optional<List<Food>> getCheatDayFood(int remainingKcal, String lackingNutrientName, String category);
}
