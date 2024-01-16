package mealplanb.server.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class PatchAvatarResponse {

    /** 아바타 수정 */
    private long userId;
    private String nickname;
    private String avatarColor;
}
