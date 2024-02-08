package mealplanb.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetKcalStatisticResponse {

    private String statisticType;
    private List<?> kcals;

    @Getter
    @AllArgsConstructor
    public static class DailyKcal{
        private LocalDate date;
        private int kcal;
        private int carbohydrate;
        private int protein;
        private int fat;
    }

    @Getter
    @AllArgsConstructor
    public static class WeeklyKcal{
        private LocalDate weekStartDate;
        private LocalDate weekEndDate;
        private int kcal;
        private int carbohydrate;
        private int protein;
        private int fat;
    }

    @Getter
    @AllArgsConstructor
    public static class MonthlyKcal{
        private YearMonth month;
        private int kcal;
        private int carbohydrate;
        private int protein;
        private int fat;
    }
}
