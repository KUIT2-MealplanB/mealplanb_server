package mealplanb.server.dto.weight;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class GetWeightRequest {

    /** 홈화면 체중 조회 */
    @NotNull(message = "date: {NotNull}")
    private LocalDate date;
}
