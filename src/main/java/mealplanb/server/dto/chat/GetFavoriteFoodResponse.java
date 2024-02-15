package mealplanb.server.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetFavoriteFoodResponse {
    private long foodId;
    private String name;
    private int carbohydrate;
    private int protein;
    private int fat;
}
