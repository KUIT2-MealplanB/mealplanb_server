package mealplanb.server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchMemberResponse {
    /** 회원 탈퇴 */
    private Long memberId;
    private String status; // 'D'
}
