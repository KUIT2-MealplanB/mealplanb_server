package mealplanb.server.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchAvatarRequest {

    /** 아바타 수정 */
    @NotNull(message = "nickname: {NotNull}")
    private String nickname;

    @NotNull(message = "avatar_color: {NotNull}")
    private String avatarColor;
}
