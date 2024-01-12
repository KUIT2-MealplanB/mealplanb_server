package mealplanb.server.dto.user;

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

    @NotNull(message = "avartar_color: {NotNull}")
    private String avartar_color;
}
