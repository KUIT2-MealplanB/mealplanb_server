package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.dto.user.*;
import mealplanb.server.service.MemberService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.INVALID_USER_VALUE;
import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.MEMBER_NOT_FOUND;
import static mealplanb.server.util.BindingResultUtils.getErrorMessages;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    public Long extractTokenFromHeader(String authorization){
        // Authorization 헤더에서 JWT 토큰 추출
        String jwtToken = jwtProvider.extractJwtToken(authorization);

        // JWT 토큰에서 사용자 정보 추출
        Long memberId = jwtProvider.extractMemberIdFromJwtToken(jwtToken);
        return memberId;
    }

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
     * 로그인
     */
    @PostMapping("/login")
    public BaseResponse<PostLoginResponse> login(@Validated @RequestBody PostLoginRequest postLoginRequest, BindingResult bindingResult){
        log.info("[MemberController.login]");
        if (bindingResult.hasErrors()){
            throw new MemberException(MEMBER_NOT_FOUND, getErrorMessages(bindingResult));
        }
        return new BaseResponse<>(memberService.login(postLoginRequest));
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

    /**
     * 아바타 외형 수정
     */
    @PatchMapping("/avatar/appearance")
    public BaseResponse<PatchAvatarAppearanceResponse> modifyAvatarAppearance(@Validated @RequestBody PatchAvatarAppearanceRequest patchAvatarAppearanceRequest,
                                                                              @RequestHeader("Authorization") String authorization){
        log.info("[MemberController.modifyAvatarAppearance]");
        Long memberId = extractTokenFromHeader(authorization);
        return new BaseResponse<>(memberService.modifyAvatarAppearance(memberId,patchAvatarAppearanceRequest));
    }
}
