package mealplanb.server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class GetFoodHistoryResponse {

    /** 섭취한 칼로리, 탄단지 일간 조회 일간 조회 */

    @Getter
    @AllArgsConstructor
    public static class DailyFoodHistory{
        private LocalDate date;
        private double calories;
        private double carbohydrates;
        private double protein;
        private double fat;
    }

    @Getter
    @AllArgsConstructor
    public static class DailyFoodHistoryResponse{
        private List<DailyFoodHistory> dailySummary;
        private String dietType;
        private double goalWeight;
    }

    /** 섭취한 칼로리, 탄단지 주간 조회 */
    @Getter
    @AllArgsConstructor
    public static class WeeklyFoodHistory{
        private LocalDate weekStartDate;
        private LocalDate weekEndDate;
        private double total_calories;
        private double total_carbohydrates;
        private double total_protein;
        private double total_fat;
    }


    @Getter
    @AllArgsConstructor
    public static class WeeklyFoodHistoryResponse{
        private List<WeeklyFoodHistory> weeklySummary;
        private String dietType;
        private double goalWeight;
    }

    /** 섭취한 칼로리, 탄단지 월간 조회 */
    @Getter
    @AllArgsConstructor
    public static class MonthlyFoodHistory{
        private YearMonth month;
        private double average_calories;
        private double average_carbohydrates;
        private double average_protein;
        private double average_fat;
    }


    @Getter
    @AllArgsConstructor
    public static class MonthlyFoodHistoryResponse{
        private List<MonthlyFoodHistory> monthlySummary;
        private String dietType;
        private double goalWeight;
    }
}
