package mealplanb.server.dto.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mealplanb.server.domain.Food.Food;

import java.util.List;

import static mealplanb.server.dto.food.GetFavoriteFoodResponse.*;

@Getter
@AllArgsConstructor
public class GetFoodAutoCompleteResponse {
    /** 자동완성 검색 */
    private int currentPage;
    private int lastPage;
    private List<GetFoodAutoCompleteFoodItem> foods;

    @Getter
    @Setter
    public static class GetFoodAutoCompleteFoodItem {
        private Long foodId;
        private String foodName;
        private int kcal;
        private boolean isMemberCreated;

        public GetFoodAutoCompleteFoodItem(Food food) {
            this.foodId = food.getFoodId();
            this.foodName = food.getName();
            this.kcal = (int) food.getKcal();
            this.isMemberCreated = food.getCreateMemberId()==null?false:true;
        }
    }
}
