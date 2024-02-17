package mealplanb.server.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetFavoriteFoodResponse {
    private long foodId;
    private String name;
    private String offer;
    private int offerCarbohydrate;
    private int offerProtein;
    private int offerFat;
}
