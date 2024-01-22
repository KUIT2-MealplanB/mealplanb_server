package mealplanb.server.repository;

import mealplanb.server.domain.FoodFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodFavoriteRepository extends JpaRepository<FoodFavorite, Long> {
    boolean existsByFoodIdAndMemberIdAndStatus(Long foodId, Long memberId, String status); // 즐겨찾기 여부 확인용
}
