package mealplanb.server.repository;

import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FavoriteMeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteMealRepository extends JpaRepository<FavoriteMeal, Long> {

    boolean existsByFavoriteMealNameAndStatus(String favoriteMealName, BaseStatus A);
    boolean existsByFavoriteMealIdAndStatus(Long favoriteMealId, BaseStatus A);
    Optional<List<FavoriteMeal>> findByMember_MemberIdAndStatus(Long memberId, BaseStatus A);
    Optional<FavoriteMeal> findByMember_MemberIdAndFavoriteMealIdAndStatus(Long memberId, Long favoriteMealID, BaseStatus A);
}