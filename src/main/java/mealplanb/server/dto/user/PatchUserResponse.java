package mealplanb.server.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchUserResponse {
    /** 회원 탈퇴 */
    private Long userId;
    private String status; // 'D'
}
