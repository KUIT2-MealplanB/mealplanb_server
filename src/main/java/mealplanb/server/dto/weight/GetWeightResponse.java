package mealplanb.server.dto.weight;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetWeightResponse {

    /** 홈화면 체중 조회 */
    private double weight;
}
