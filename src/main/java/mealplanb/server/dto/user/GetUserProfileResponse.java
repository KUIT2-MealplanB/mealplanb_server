package mealplanb.server.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetUserProfileResponse {

    /** 아바타, 목표 칼로리 및 잔여 칼로리, 탄단지 기타 영양소 조회 */
    private String avatar_color;
    private String avatar_appearance;
    private Goal goal;
    private Intake intake;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Goal{
        private int target_kcal;
        private int target_carbohydrate;
        private int target_protein;
        private int target_fat;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Intake{
        private int kcal;
        private int carbohydrate;
        private int protein;
        private int fat;
        private int sodium;
        private int sugar;
        private int saturated_fat;
        private int trans_fat;
        private int cholesterol;
    }

}
