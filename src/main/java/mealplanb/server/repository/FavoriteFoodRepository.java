package mealplanb.server.repository;

import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FavoriteFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteFoodRepository extends JpaRepository<FavoriteFood, Long> {
    boolean existsByFood_FoodIdAndMember_MemberIdAndStatus(long foodId, long memberId, BaseStatus a);
}
