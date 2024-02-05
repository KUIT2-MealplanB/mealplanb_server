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
    public static class WeightsStatisticResponse{
        private String statisticType;
        private LocalDate startDate;
        private List<WeightResponse> weights;
    }

/*    *//** 체중 주간 조회 *//*
    @Getter
    @AllArgsConstructor
    public static class WeeklyWeight{
        private LocalDate weekStartDate;
        private LocalDate weekEndDate;
        private double averageWeight;
    }


    @Getter
    @AllArgsConstructor
    public static class WeeklyWeightResponse{
        private List<WeeklyWeight> weeklyWeights;
        private String dietType;
        private double goalWeight;
    }

    *//** 체중 월간 조회 *//*
    @Getter
    @AllArgsConstructor
    public static class MonthlyWeight{
        private YearMonth month;
        private double averageWeight;
    }


    @Getter
    @AllArgsConstructor
    public static class MonthlyWeightResponse{
        private List<MonthlyWeight> monthlyWeights;
        private String dietType;
        private double goalWeight;
    }*/

}
