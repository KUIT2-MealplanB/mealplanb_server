package mealplanb.server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetAvatarResponse {

    /** 아바타 정보 조회 */
    private String avatarAppearance;
    private String avatarColor;
    private String nickname;
}
