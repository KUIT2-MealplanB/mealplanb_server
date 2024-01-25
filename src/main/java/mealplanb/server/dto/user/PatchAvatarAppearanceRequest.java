package mealplanb.server.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchAvatarAppearanceRequest {

    /** 아바타 외형 수정 */
    private int skeletalMuscleMass;
    private int fatMass;
}
