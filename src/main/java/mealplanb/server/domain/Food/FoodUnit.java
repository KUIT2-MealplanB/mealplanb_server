package mealplanb.server.domain.Food;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FoodUnit {
    /**
     * unitGram : 단위에 해당하는 gram
     * unitName : 단위를 지칭하는 이름
     *
     * ex) 피자가 100g에 한조각이면 unitGram은 100, unitName은 "g"
     */
    private final int unitGram;
    private final String unitName;
}
