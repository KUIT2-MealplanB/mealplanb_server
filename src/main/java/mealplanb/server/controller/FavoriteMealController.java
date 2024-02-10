package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.dto.meal.PostMyMealRequest;
import mealplanb.server.service.FavoriteMealService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/my-meal")
public class FavoriteMealController {

    private final JwtProvider jwtProvider;
    private final FavoriteMealService favoriteMealService;

    /**
     * 나의 식단 등록
     */
    @PostMapping("")
    public BaseResponse<Void> postMyMeal(@RequestHeader("Authorization") String authorization,
                                         @RequestBody PostMyMealRequest postMyMealRequest){
        log.info("[MyMealController.postMyMeal]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        favoriteMealService.postMyMeal(memberId, postMyMealRequest);
        return new BaseResponse<>(null);
    }
}
