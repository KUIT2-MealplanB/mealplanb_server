package mealplanb.server.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostMemberResponse {

    /** 회원 가입 */
    private Long memberId;
    private String jwt;
}
