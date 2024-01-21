package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.dto.user.GetAvatarResponse;
import mealplanb.server.service.MemberService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    /**
     * 아바타 정보 조회
     * */
    @GetMapping("/avatar")
    public BaseResponse<GetAvatarResponse> getAvatarInfo(@RequestHeader("Authorization") String authorization){
        log.info("[MemberController.getAvatarInfo]");
        // Authorization 헤더에서 JWT 토큰 추출
        String jwtToken = jwtProvider.extractJwtToken(authorization);

        // JWT 토큰에서 사용자 정보 추출
        Long memberId = jwtProvider.extractMemberIdFromJwtToken(jwtToken);

        return new BaseResponse<>(memberService.getAvatarResponse(memberId));
    }
}
