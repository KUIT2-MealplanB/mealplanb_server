package mealplanb.server.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserJoinResponse {

    /** 회원 가입 */
    private Long userId;
    private String jwt;
}
