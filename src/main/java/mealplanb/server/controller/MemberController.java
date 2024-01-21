package mealplanb.server.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.dto.user.PostUserRequest;
import mealplanb.server.dto.user.PostUserResponse;
import mealplanb.server.service.MemberService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.INVALID_USER_VALUE;
import static mealplanb.server.util.BindingResultUtils.getErrorMessages;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 가입
     */
    @PostMapping("/signup")
    public BaseResponse<PostUserResponse> signup(@Validated @RequestBody PostUserRequest postUserRequest, BindingResult bindingResult){
        log.info("[MemberController.signup]");
        if (bindingResult.hasErrors()) {
            throw new MemberException(INVALID_USER_VALUE, getErrorMessages(bindingResult));
        }
        return new BaseResponse<>(memberService.signUp(postUserRequest));
    }
}
