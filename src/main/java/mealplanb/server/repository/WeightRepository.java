package mealplanb.server.repository;

import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.domain.Weight;
import mealplanb.server.dto.weight.GetWeightStatisticResponse;
import mealplanb.server.dto.weight.GetWeightStatisticResponse.WeeklyWeight;
import mealplanb.server.dto.weight.WeightResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.reflect.WildcardType;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

public interface WeightRepository extends JpaRepository<Weight, Long> {

    Optional<Weight> findTopByMemberOrderByWeightDateDesc(Member member);
    Optional<Weight> findTopByMember_MemberIdOrderByWeightDateDesc(Long memberId);
    Optional<Weight> findByMemberAndWeightDate(Member member, LocalDate date);
    Optional<List<Weight>> findAllByMemberAndStatusOrderByWeightDate(Member member, BaseStatus a);

    @Query(value = "SELECT " +
            "AVG(w.weight) AS weekAverageWeight, " +
            "DATE_FORMAT(DATE_ADD(w.weight_date, INTERVAL -WEEKDAY(w.weight_date) DAY), '%Y-%m-%d') AS weekStartDate, " +
            "DATE_FORMAT(DATE_ADD(w.weight_date, INTERVAL (6 - WEEKDAY(w.weight_date)) DAY), '%Y-%m-%d') AS weekEndDate " +
            "FROM " +
            "weight w " +
            "WHERE " +
            "w.member_id = :memberId AND w.status = :#{#status.name()} " +
            "GROUP BY " +
            "weekStartDate, weekEndDate " +
            "ORDER BY " +
            "weekStartDate ASC", nativeQuery = true)
    Optional<List<WeeklyWeightNativeVo>> findWeeklyWeights(@Param("memberId") Long memberId, @Param("status") BaseStatus status);

    interface WeeklyWeightNativeVo {
        Double getWeekAverageWeight();
        String getWeekStartDate();
        String getWeekEndDate();
    }
}