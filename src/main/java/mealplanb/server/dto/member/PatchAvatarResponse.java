package mealplanb.server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchAvatarResponse {

    /** 아바타 수정 */
    private long memberId;
    private String nickname;
    private String avatarColor;
}
