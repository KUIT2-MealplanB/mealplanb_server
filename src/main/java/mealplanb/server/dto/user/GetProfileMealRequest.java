package mealplanb.server.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class GetProfileMealRequest {

    /** 홈화면 식사 조회 */
    @NotNull(message = "date: {NotNull}")
    private LocalDate date;
}
