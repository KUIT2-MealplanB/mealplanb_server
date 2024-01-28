package mealplanb.server.dto.meal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostMealResponse {
    /** 끼니 생성 */
    private Long mealId;
}
