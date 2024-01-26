package mealplanb.server.repository;

import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FoodFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodFavoriteRepository extends JpaRepository<FoodFavorite, Long> {
    boolean existsByFood_FoodIdAndMember_MemberIdAndStatus(long foodId, long memberId, BaseStatus a);
}
