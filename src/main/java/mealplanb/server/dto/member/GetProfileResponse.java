package mealplanb.server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class GetProfileResponse {

    /** 홈화면 현재 날짜, 목표 경과일, 남은 칼로리 조회
    * 아바타, 목표 칼로리 및 잔여 칼로리, 탄단지 기타 영양소 조회 */
    private LocalDate date;
    private int elapsedDays;
    private int remainingKcal;
    private String avatarColor;
    private String avatarAppearance;
    private Goal goal;
    private Intake intake;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Goal{

        private int targetKcal;
        private int targetCarbohydrate;
        private int targetProtein;
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
        private int saturatedFat;
        private int transFat;
        private int cholesterol;
    }

}
