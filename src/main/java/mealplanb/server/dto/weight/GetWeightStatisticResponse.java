package mealplanb.server.dto.weight;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class GetWeightStatisticResponse {

    /** 체중 일간, 월간, 주간 조회 */
    @Getter
    @AllArgsConstructor
    public static class WeightStatisticResponse{
        private String statisticType;
        private List<?> weights;
    }

    /** 체중 일간 조회의 weights */
    // => WeightResponse를 사용

    /** 체중 주간 조회의 weights */
    @Getter
    @AllArgsConstructor
    public class WeeklyWeight {
        private double averageWeight;
        private LocalDate weekStartDate;
        private LocalDate weekEndDate;
    }

    /** 체중 주간 조회의 weights */
    @Getter
    @AllArgsConstructor
    public static class MonthlyWeight{
        private double averageWeight;
        private YearMonth month;
    }

}
