package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.dto.food.GetFavoriteFoodResponse;
import mealplanb.server.dto.food.PostFavoriteFoodRequest;
import mealplanb.server.service.FavoriteFoodService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite-food")
public class FavoriteFoodController {

    private final FavoriteFoodService favoriteFoodService;
    private final JwtProvider jwtProvider;

    /**
     * 즐겨찾기 식사 등록
     */
    @PostMapping("")
    public BaseResponse<Void> postFavoriteFood(@RequestHeader("Authorization") String authorization,
                                               @RequestBody PostFavoriteFoodRequest postFavoriteFoodRequest){
        log.info("[MemberController.postFavoriteFood]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        favoriteFoodService.postFavoriteFood(memberId, postFavoriteFoodRequest);
        return new BaseResponse<>(null);
    }

    /**
     * 즐겨찾기 식사 조회
     */
    @GetMapping("")
    public BaseResponse<List<GetFavoriteFoodResponse>> getFavoriteFoodList(@RequestHeader("Authorization") String authorization){
        log.info("[MemberController.getFavoriteFoodList]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(favoriteFoodService.getFavoriteFoodList(memberId));
    }

    /**
     * 즐겨찾기 해제
     */
    @PatchMapping("/{foodId}")
    public BaseResponse<Void> deleteFavoriteFood(@RequestHeader("Authorization") String authorization,
                                                 @PathVariable Long foodId){
        log.info("[MemberController.deleteFavoriteFood]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        favoriteFoodService.deleteFavoriteFood(memberId, foodId);
        return new BaseResponse<>(null);
    }
}
