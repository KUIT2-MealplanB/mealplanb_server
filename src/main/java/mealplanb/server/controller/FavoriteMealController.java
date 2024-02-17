package mealplanb.server.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.dto.meal.GetMyMealListResponse;
import mealplanb.server.dto.meal.GetMyMealResponse;
import mealplanb.server.dto.meal.PostMyMealRequest;
import mealplanb.server.service.FavoriteMealService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 나의 식단 조회
     */
    @GetMapping("")
    public BaseResponse<List<GetMyMealResponse>> getMyMeal(@RequestHeader ("Authorization") String authorization){
        log.info("[FavoriteMealController.getMyMeal]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(favoriteMealService.getMyMeal(memberId));
    }

    /**
     * 나의 식단 삭제
     */

    @PatchMapping("/{favoriteMealId}")
    public BaseResponse<Void> deleteMyMeal(@RequestHeader("Authorization") String authorization,
                                           @PathVariable Long favoriteMealId){
        log.info("[FavoriteMealController.deleteMyMeal]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        favoriteMealService.deleteMyMeal(memberId, favoriteMealId);
        return new BaseResponse<>(null);
    }

    /**
     * 나의 식단 선택해서 식사 리스트 조회하기
     */
    @GetMapping("/{favoriteMealId}")
    public BaseResponse<List<GetMyMealListResponse>> getMyMealList(@RequestHeader("Authorization") String authorization,
                                                                   @PathVariable Long favoriteMealId){
        log.info("[FavoriteMealController.getMyMealList]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(favoriteMealService.getMyMealList(memberId, favoriteMealId));
    }

}