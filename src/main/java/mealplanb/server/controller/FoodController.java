package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.dto.food.GetFoodResponse;
import mealplanb.server.dto.food.PostNewFoodRequest;
import mealplanb.server.dto.food.PostNewFoodResponse;
import mealplanb.server.service.FoodService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.INVALID_USER_VALUE;
import static mealplanb.server.util.BindingResultUtils.getErrorMessages;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/food")
public class FoodController {
    private final FoodService foodService;
    private final JwtProvider jwtProvider;

    /**
     * 특정 식사의 상세 정보 반환
     */
    @GetMapping("/{foodId}")
    public BaseResponse<GetFoodResponse> getFoodDetail(@RequestHeader("Authorization") String authorization,
                                                       @PathVariable long foodId) {
        System.out.println("[FoodController.getFoodDetail]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(foodService.getFoodDetail(memberId, foodId));
    }

    /**
     * 식사 등록 by 사용자
     */
    @PostMapping("")
    public BaseResponse<PostNewFoodResponse> postNewFood(@RequestHeader("Authorization") String authorization,
                                                         @Validated @RequestBody PostNewFoodRequest postNewFoodRequest,
                                                         BindingResult bindingResult){
        System.out.println("[FoodController.postNewFood]");
        if (bindingResult.hasErrors()) {
            throw new MemberException(INVALID_USER_VALUE, getErrorMessages(bindingResult));
        }
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(foodService.postNewFood(memberId, postNewFoodRequest));
    }

    /**
     * 사용자 등록 식품(=식사) 삭제
     */
    @PatchMapping("/{foodId}")
    public BaseResponse<Void> deleteUserCreatedFood(@RequestHeader("Authorization") String authorization,
                                                    @PathVariable long foodId) {
        System.out.println("[FoodController.deleteUserCreatedFood]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        foodService.deleteUserCreatedFood(memberId, foodId);
        return new BaseResponse<>(null);
    }

}
