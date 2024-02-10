package mealplanb.server.repository;

import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FavoriteFood;
import mealplanb.server.domain.Food;
import mealplanb.server.domain.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteFoodRepository extends JpaRepository<FavoriteFood, Long> {

    boolean existsByFood_FoodIdAndMember_MemberIdAndStatus(long foodId, long memberId, BaseStatus a);
    Optional<List<FavoriteFood>> findByMember_MemberIdAndStatus(long memberId, BaseStatus A);
    Optional<FavoriteFood> findByMember_MemberIdAndFood_FoodIdAndStatus(long memberId, long foodId, BaseStatus A);
    boolean existsByMemberAndFoodAndStatus(Member member, Food food, BaseStatus A);
}
