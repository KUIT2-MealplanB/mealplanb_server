package mealplanb.server.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchAvatarRequest {

    @NotNull(message = "nickname: {NotNull}")
    private String nickname;

    @NotNull(message = "avatar_color: {NotNull}")
    @JsonProperty("avatar_color")
    private String avatarColor;
}
