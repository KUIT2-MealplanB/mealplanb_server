package mealplanb.server.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetProfileResponse {

    /** 아바타, 목표 칼로리 및 잔여 칼로리, 탄단지 기타 영양소 조회 */
    @JsonProperty("avatar_color")
    private String avatarColor;

    @JsonProperty("avatar_appearance")
    private String avatarAppearance;

    private Goal goal;
    private Intake intake;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Goal{

        @JsonProperty("target_kcal")
        private int targetKcal;

        @JsonProperty("target_carbohydrate")
        private int targetCarbohydrate;

        @JsonProperty("target_protein")
        private int targetProtein;

        @JsonProperty("target_fat")
        private int targetFat;
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

        @JsonProperty("saturated_fat")
        private int saturatedFat;

        @JsonProperty("trans_fat")
        private int transFat;

        private int cholesterol;
    }

}
