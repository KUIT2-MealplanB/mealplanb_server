package mealplanb.server.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    /** 로그인 */
    @NotNull(message = "provider: {NotNull}")
    private String provider; //'kakao','google','naver'

    @NotNull(message = "socialToken: {NotNull}")
    private String socialToken;
}
