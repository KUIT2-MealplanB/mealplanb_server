package mealplanb.server.repository;

import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Meal.Meal;
import mealplanb.server.domain.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {
    boolean existsByMemberAndMealDateAndMealType(Member member, LocalDate mealDate, int mealType);
    Optional<List<Meal>> findByMember_MemberIdAndMealDateAndStatus(Long memberId, LocalDate mealDate, BaseStatus status);
    Optional<Meal> findByMealIdAndMember_MemberId(long mealId, Long memberId);
    Optional<List<Meal>> findAllByMealDateAndMemberAndMealTypeGreaterThan(LocalDate mealDate, Member member, int mealType);
    Optional<Meal> findByMealIdAndStatus(Long mealId, BaseStatus status);
    @Query(value = "SELECT meal_date as date, GROUP_CONCAT(meal_id) AS mealIds " +
            "FROM meal " +
            "WHERE member_id = :memberId AND status = 'A'", nativeQuery = true)
    Optional<List<DailyKcalNativeVo>> findDailyKcal(@Param("memberId") Long memberId);

    interface DailyKcalNativeVo {
        LocalDate getDate();
        String getMealIds();
    }

}
