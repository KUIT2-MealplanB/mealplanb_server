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
            "f.kcal DESC LIMIT 10")
    Optional<List<Food>> getCheatDayFood(int remainingKcal, String lackingNutrient1, String lackingNutrient2, String lackingNutrient3, String category);


    @Query("SELECT f FROM Food f " +
            "WHERE (f.category = '라면' OR f.category = '국수') " +
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
            "f.kcal DESC LIMIT 10")
    Optional<List<Food>> getCheatDayFoodNoodle(int remainingKcal, String lackingNutrient1, String lackingNutrient2, String lackingNutrient3);

    @Query("SELECT f FROM Food f " +
            "WHERE f.category = '빵' OR f.category = '케이크' OR f.category = '마카롱' OR f.category = '비스킷/쿠키/크래커' OR f.category = '스낵과자' OR f.category = '웨이퍼' OR f.category = '일반과자' OR f.category = '크로와상' OR f.category = '타르트' OR f.category = '크로켓' OR f.category = '파이/만쥬' " +
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
            "f.kcal DESC LIMIT 10")
    Optional<List<Food>> getCheatDayFoodDessert(int remainingKcal, String lackingNutrient1, String lackingNutrient2, String lackingNutrient3);

}
