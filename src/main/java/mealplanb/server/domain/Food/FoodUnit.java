package mealplanb.server.domain.Food;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FoodUnit {
    private final int unitGram;
    private final String unitName;
}
