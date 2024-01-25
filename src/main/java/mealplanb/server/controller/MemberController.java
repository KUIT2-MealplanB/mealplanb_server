package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.dto.user.GetAvatarResponse;
import mealplanb.server.dto.user.PostMemberRequest;
import mealplanb.server.dto.user.PostMemberResponse;
import mealplanb.server.service.MemberService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.INVALID_USER_VALUE;
import static mealplanb.server.util.BindingResultUtils.getErrorMessages;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    /**
     * 회원 가입
     */
    @PostMapping("/signup")
    public BaseResponse<PostMemberResponse> signup(@Validated @RequestBody PostMemberRequest postUserRequest, BindingResult bindingResult) {
        log.info("[MemberController.signup]");
        if (bindingResult.hasErrors()) {
            throw new MemberException(INVALID_USER_VALUE, getErrorMessages(bindingResult));
        }
        return new BaseResponse<>(memberService.signUp(postUserRequest));

    }
        /**
         * 아바타 정보 조회
         * */
        @GetMapping("/avatar")
        public BaseResponse<GetAvatarResponse> getAvatarInfo (@RequestHeader("Authorization") String authorization){
            log.info("[MemberController.getAvatarInfo]");
            // Authorization 헤더에서 JWT 토큰 추출
            String jwtToken = jwtProvider.extractJwtToken(authorization);

            // JWT 토큰에서 사용자 정보 추출
            Long memberId = jwtProvider.extractMemberIdFromJwtToken(jwtToken);

            return new BaseResponse<>(memberService.getAvatarResponse(memberId));
        }
}
