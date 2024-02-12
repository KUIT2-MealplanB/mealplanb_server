package mealplanb.server.repository;

import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findByFoodIdAndStatus(long foodId, BaseStatus a);
    @Query(value = "SELECT * " +
            "FROM food " +
            "WHERE name LIKE %:query% AND status = 'A'" , nativeQuery = true)
    Page<Food> getAutoComplete(@Param("query") String query, Pageable pageable);
}
