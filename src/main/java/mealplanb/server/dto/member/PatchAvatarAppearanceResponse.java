package mealplanb.server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchAvatarAppearanceResponse {

    /** 아바타 외형 수정 */
    private String avatarAppearance;
}
