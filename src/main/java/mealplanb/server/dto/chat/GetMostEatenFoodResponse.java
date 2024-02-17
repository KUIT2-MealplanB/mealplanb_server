package mealplanb.server.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetMostEatenFoodResponse {
    /** 채팅(자주먹은), 채팅(인기있는)*/
    private long foodId;
    private String name;
    private String offer;
    private int offerQuantity;
    private int offerCarbohydrate;
    private int offerProtein;
    private int offerFat;
}
