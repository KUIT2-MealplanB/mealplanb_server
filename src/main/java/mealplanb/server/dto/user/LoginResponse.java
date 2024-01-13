package mealplanb.server.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    /** 로그인 */
    @JsonProperty("user_id")
    private Long userId;

    private String jwt;
}
