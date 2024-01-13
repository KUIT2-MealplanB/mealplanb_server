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

    @JsonProperty("user_id")
    private long userId;

    private String nickname;

    @JsonProperty("avatar_color")
    private String avatarColor;
}
