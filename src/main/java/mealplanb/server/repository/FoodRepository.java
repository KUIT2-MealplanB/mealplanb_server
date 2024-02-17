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
            "WHERE name LIKE %:query% AND status = 'A' " +
            "AND (create_member_id = :memberId OR admin_approval = true)", nativeQuery = true)
    Page<Food> getAutoComplete(@Param("memberId") Long memberId, @Param("query") String query, Pageable pageable);

    @Query("SELECT f FROM Food f " +
            "WHERE f.category = ?5 " +
            "AND f.kcal BETWEEN 0 AND ?1 " +
            "ORDER BY " +
            "CASE " +
            "WHEN ?2 = '탄수화물' THEN f.carbohydrate " +
            "WHEN ?2 = '단백질' THEN f.protein " +
            "WHEN ?2 = '지방' THEN f.fat " +
            "ELSE 0 " +
            "END DESC, " +
            "CASE " +
            "WHEN ?3 = '탄수화물' THEN f.carbohydrate " +
            "WHEN ?3 = '단백질' THEN f.protein " +
            "WHEN ?3 = '지방' THEN f.fat " +
            "ELSE 0 " +
            "END DESC, " +
            "CASE " +
            "WHEN ?4 = '탄수화물' THEN f.carbohydrate " +
            "WHEN ?4 = '단백질' THEN f.protein " +
            "WHEN ?4 = '지방' THEN f.fat " +
            "ELSE 0 " +
            "END DESC, " +
            "f.kcal DESC")
    Optional<List<Food>> getCheatDayFood(int remainingKcal, String lackingNutrient1, String lackingNutrient2, String lackingNutrient3, String category);
}
