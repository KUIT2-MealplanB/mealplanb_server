package mealplanb.server.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class GetHomeResponse {

    /** 홈화면 현재날짜, 목표 경과일, 남은 칼로리 조회 */
    private LocalDate date;
    private String day;
    private String nickname;

    @JsonProperty("elapsed_days")
    private int elapsedDays;

    @JsonProperty("remaining_kcal")
    private int remainingKcal;
}
