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
            "WHERE member_id = :memberId AND status = 'A'" +
            "GROUP BY date", nativeQuery = true)
    Optional<List<DailyKcalNativeVo>> findDailyKcal(@Param("memberId") Long memberId);

    interface DailyKcalNativeVo {
        LocalDate getDate();
        String getMealIds();
    }

    @Query(value = "SELECT " +
            "DATE_FORMAT(DATE_ADD(meal_date, INTERVAL -WEEKDAY(meal_date) DAY), '%Y-%m-%d') as weekStartDate, " +
            "DATE_FORMAT(DATE_ADD(meal_date, INTERVAL (6 - WEEKDAY(meal_date)) DAY), '%Y-%m-%d') as weekEndDate, " +
            "COUNT(DISTINCT meal_date) AS daysInWeek, " +
            "GROUP_CONCAT(meal_id) AS mealIds " +
            "FROM meal " +
            "WHERE member_id = :memberId AND status = 'A' " +
            "GROUP BY WEEK(meal_date)", nativeQuery = true)
    Optional<List<WeeklyKcalNativeVo>> findWeeklyKcal(@Param("memberId")Long memberId);

    interface WeeklyKcalNativeVo {
        String getWeekStartDate();
        String getWeekEndDate();
        Integer getDaysInWeek();
        String getMealIds();
    }

    @Query(value = "SELECT " +
            "DATE_FORMAT(meal_date, '%Y-%m') as month, " +
            "COUNT(DISTINCT meal_date) AS daysInMonth, " +
            "GROUP_CONCAT(meal_id) AS mealIds " +
            "FROM meal " +
            "WHERE member_id = :memberId AND status = 'A' " +
            "GROUP BY month", nativeQuery = true)
    Optional<List<MonthlyKcalNativeVo>> findMonthlyKcal(@Param("memberId")Long memberId);

    interface MonthlyKcalNativeVo {
        String getMonth();
        Integer getDaysInMonth();
        String getMealIds();
    }

}
