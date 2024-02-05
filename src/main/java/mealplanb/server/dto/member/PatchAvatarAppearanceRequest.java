package mealplanb.server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchAvatarAppearanceRequest {

    /** 아바타 외형 수정 */
    private int skeletalMuscleMass;
    private int fatMass;
}