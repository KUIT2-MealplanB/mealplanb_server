package mealplanb.server.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class GetAvatarResponse {

    /** 아바타 정보 조회 */
    private String avatarAppearance;
    private String avatarColor;
    private String nickname;
}
