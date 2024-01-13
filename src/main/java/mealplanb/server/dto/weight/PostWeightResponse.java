package mealplanb.server.dto.weight;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PostWeightResponse {

    /** 체중 등록 */
    private double weight;
    private LocalDate date;
}
