package mealplanb.server.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetMealSuggestedFoodResponse {
    /** 채팅을 통해 추천받은 끼니 조회 (등록식단 모아보기) */
    private String date;
    private String mealType;
    private String name;
    private int offerCarbohydrate;
    private int offerProtein;
    private int offerFat;
}
