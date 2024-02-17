package mealplanb.server.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetCheatDayFoodResponse {
    /** 채팅(치팅데이) */
    private List<cheatDayFoodInfo> cheatDayFood;

    @Getter
    @AllArgsConstructor
    public static class cheatDayFoodInfo {
        private long foodId;
        private String name;
        private String offer;
        private int quantity;
        private int offerCarbohydrate;
        private int offerProtein;
        private int offerFat;
    }
}
