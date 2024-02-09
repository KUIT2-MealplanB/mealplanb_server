package mealplanb.server.dto.food;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostFavoriteFoodRequest {

    /**
     * 즐겨찾기 식사 등록
     */
    //private String name;
    private Long foodId;
}
