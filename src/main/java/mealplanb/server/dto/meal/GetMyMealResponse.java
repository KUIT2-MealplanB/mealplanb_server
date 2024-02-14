package mealplanb.server.dto.meal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetMyMealResponse {
    /**
     * 나의 식단 조회
     */
    private List<FavoriteMealItem> mealItemList;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class FavoriteMealItem{
        private long favoriteMealId;
        private String favoriteMealName;
        private int mealKcal;
        private int foodCount; // 내용물 개수

        public FavoriteMealItem(Long favoriteMealId, String favoriteMealName, int totalKcal) {
            this.favoriteMealId = favoriteMealId;
            this.favoriteMealName = favoriteMealName;
            this.mealKcal = totalKcal;
        }
    }
}
