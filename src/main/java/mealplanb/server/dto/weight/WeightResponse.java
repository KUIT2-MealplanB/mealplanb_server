package mealplanb.server.dto.weight;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class WeightResponse {

    /** 체중 관련 response */
    private double weight;
    private LocalDate date;
}
