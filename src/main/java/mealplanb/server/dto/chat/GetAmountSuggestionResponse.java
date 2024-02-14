package mealplanb.server.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetAmountSuggestionResponse {
    private String foodName;
    private String offer;
    private int offerKcal;
    private int remainingKcal;
}
