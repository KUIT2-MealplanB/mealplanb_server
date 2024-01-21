package mealplanb.server.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostUserResponse {

    /** 회원 가입 */
    private Long userId;
    private String jwt;
}
