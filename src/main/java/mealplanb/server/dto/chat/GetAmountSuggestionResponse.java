package mealplanb.server.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetAmountSuggestionResponse {
    /** 얼마나 먹을까요 */
    private String foodName;
    private String offer;
    private int offerKcal;
    private int offerCarbohydrate;
    private int offerProtein;
    private int offerFat;
    private int remainingKcal;
}
