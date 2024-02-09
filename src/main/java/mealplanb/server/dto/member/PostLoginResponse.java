package mealplanb.server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLoginResponse {
    /** 로그인 */
    private Long memberId;
    private String jwt;
}
