package mealplanb.server.dto.meal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetMyMealListResponse {
    /**
     * 나의 식단 선택해서 식사 리스트 조회하기
     */
    private List<GetMyMealItem> myMealItemList;

    @Getter
    @NoArgsConstructor
    public static class GetMyMealItem{
        private long foodId;
        private String foodName;
        private int quantity;
        private int kcal;

        public GetMyMealItem(long foodId, String foodName, int quantity, int kcal){
            this.foodId = foodId;
            this.foodName = foodName;
            this.quantity = quantity;
            this.kcal = kcal;
        }
    }


}
