package mealplanb.server.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class GetUserHomeRequest {

    /** 홈화면 현재날짜, 목표 경과일, 남은 칼로리 조회 */
    @NotNull(message = "date: {NotNull}")
    private LocalDate date;
}
