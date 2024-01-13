package mealplanb.server.dto.weight;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PostWeightRequest {

    /** 체중 등록 */
    @NotNull(message = "weight: {NotNull}")
    private double weight;

    @NotNull(message = "date: {NotNull}")
    private LocalDate date;
}
