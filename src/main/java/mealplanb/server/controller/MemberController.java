package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.dto.member.*;
import mealplanb.server.service.MemberService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(memberService.getAvatarResponse(memberId));
    }

    /**
     * 아바타 수정
     */
    @PatchMapping("/avatar")
    public BaseResponse<PatchAvatarResponse> modifyAvatar (@Validated @RequestBody PatchAvatarRequest patchAvatarRequest, @RequestHeader("Authorization") String authorization) {
        log.info("[MemberController.modifyAvatar]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(memberService.modifyAvatar(memberId, patchAvatarRequest));
    }

    /**
     * 아바타 외형 수정
     */
    @PatchMapping("/avatar/appearance")
    public BaseResponse<PatchAvatarAppearanceResponse> modifyAvatarAppearance(@Validated @RequestBody PatchAvatarAppearanceRequest patchAvatarAppearanceRequest,
            @RequestHeader("Authorization") String authorization){
        log.info("[MemberController.modifyAvatarAppearance]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(memberService.modifyAvatarAppearance(memberId,patchAvatarAppearanceRequest));
    }

    /**
     * 사용자 목표 조회
     */
    @GetMapping("/plan")
    public BaseResponse<GetPlanResponse> getMemberPlan(@RequestHeader("Authorization") String authorization){
        log.info("[MemberController.getMemberPlan]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(memberService.getMemberPlan(memberId));
    }

    /**
     * 사용자 목표 수정
     */
    @PatchMapping("/plan")
    public BaseResponse<PatchPlanResponse> modifyMemberPlan(@Validated @RequestBody PatchPlanRequest patchPlanRequest,
                                                            @RequestHeader("Authorization") String authorization){
        log.info("[MemberController.modifyMemberPlan]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(memberService.modifyMemberPlan(memberId, patchPlanRequest));
    }

    /**
     * 사용자 목표 조회 (식단타입에 따른 탄단지 조회)
     */
    @GetMapping("/plan/diet-type")
    public BaseResponse<GetDietTypeResponse> getDietType(@Validated @RequestBody GetDietTypeRequest getDietTypeRequest,
                                                         @RequestHeader("Authorization") String authorization){
        log.info("[MemberController.getDietType]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(memberService.getDietType(memberId, getDietTypeRequest));
    }

    /**
     * 사용자 목표 조회 (권장 칼로리 반환)
     */
    @GetMapping("/plan/recommended-kcal")
    public BaseResponse<GetRecommendedKcalResponse> getRecommendedKcal(@Validated @RequestBody GetRecommendedKcalRequest getRecommendedKcalRequest,
                                                                       @RequestHeader("Authorization") String authorization){
        log.info("[MemberController.getRecommendedKcal]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(memberService.getRecommendedKcal(memberId,getRecommendedKcalRequest));
     * 홈화면 현재 날짜, 목표 경과일, 남은 칼로리 조회, 아바타, 목표 칼로리 및 잔여 칼로리, 탄단지 기타 영양소 조회
     */
    
    @GetMapping("/profile")
    public BaseResponse<GetProfileResponse> getMemberProfile(@RequestHeader("Authorization") String authorization,
                                                             @RequestParam(name = "mealDate") LocalDate mealDate){
        log.info("[MemberController.getMemberProfile]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(memberService.getMemberProfile(memberId, mealDate));
    }
}
