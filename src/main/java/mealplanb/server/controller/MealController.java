package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import mealplanb.server.common.exception.MealException;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.dto.meal.GetMealFoodResponse;
import mealplanb.server.dto.meal.GetMealResponse;
import mealplanb.server.dto.meal.PostMealRequest;
import mealplanb.server.dto.meal.PostMealResponse;
import mealplanb.server.service.MealService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.INVALID_USER_VALUE;
import static mealplanb.server.util.BindingResultUtils.getErrorMessages;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meal")
public class MealController {
    private final MealService mealService;
    private final JwtProvider jwtProvider;

    /**
     *  끼니 생성
     */
    @PostMapping
    public BaseResponse<PostMealResponse> postMeal(@RequestHeader("Authorization") String authorization, @Validated @RequestBody PostMealRequest postMealRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new MealException(INVALID_USER_VALUE, getErrorMessages(bindingResult));
        }
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<PostMealResponse>(mealService.postMeal(memberId, postMealRequest));
    }

    /**
     *  끼니 기록 조회 (홈화면)ㅎ
     */
    @GetMapping
    public BaseResponse<GetMealResponse> getMealList(@RequestHeader("Authorization") String authorization,
                                                     @RequestParam(name = "mealDate") LocalDate mealDate){
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<GetMealResponse>(mealService.getMealList(memberId, mealDate));
    }

    /**
     *  끼니의 식사 리스트 조회
     */
    @GetMapping("/{mealId}/food")
    public BaseResponse<GetMealFoodResponse> getMealFoodList(@RequestHeader("Authorization") String authorization,
                                                             @PathVariable long mealId){
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<GetMealFoodResponse>(mealService.getMealFoodList(memberId, mealId));
    }
}
