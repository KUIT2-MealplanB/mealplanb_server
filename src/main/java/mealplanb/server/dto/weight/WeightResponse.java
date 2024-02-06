package mealplanb.server.dto.weight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class WeightResponse {

    /** 체중 관련 response */
    private double weight;
    private LocalDate date;
}
