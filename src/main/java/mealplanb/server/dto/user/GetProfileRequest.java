package mealplanb.server.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class GetProfileRequest {

    /** 아바타, 목표 칼로리 및 잔여 칼로리, 탄단지 기타 영양소 조회 */
    @NotNull(message = "date : {NotNull}")
    private LocalDate date;
}
