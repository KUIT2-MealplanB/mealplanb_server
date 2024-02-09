package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FavoriteFood;
import mealplanb.server.domain.Food;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.food.GetFavoriteFoodResponse;
import mealplanb.server.dto.food.PostFavoriteFoodRequest;
import mealplanb.server.repository.FavoriteFoodRepository;
import mealplanb.server.repository.FoodRepository;
import mealplanb.server.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.FOOD_NOT_FOUND;
import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteFoodService {
    private final MemberRepository memberRepository;
    private final FavoriteFoodRepository favoriteFoodRepository;
    private final FoodRepository foodRepository;

    /**
     * 즐겨찾기한 음식인지 여부 반환
     */
    public Boolean isFavorite(long memberId, long foodId){
        System.out.println("[FavoriteFoodService.isFavorite]");
        boolean isFavorite = favoriteFoodRepository.existsByFood_FoodIdAndMember_MemberIdAndStatus(foodId, memberId, BaseStatus.A);
        return isFavorite;
    }

    /**
     * 즐겨찾기 식사 등록
     */
    @Transactional
    public void postFavoriteFood(Long memberId, PostFavoriteFoodRequest postFavoriteFoodRequest){
        log.info("[FavoriteFoodService.postFavoriteFood]");

        Long foodId = postFavoriteFoodRequest.getFoodId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        Food food = foodRepository.findByFoodId(foodId)
                .orElseThrow(()-> new FoodException(FOOD_NOT_FOUND));

        FavoriteFood favoriteFood = FavoriteFood.builder()
                .member(member)
                .food(food)
                .status(BaseStatus.A)
                .build();

        favoriteFoodRepository.save(favoriteFood);
    }

    /**
     * 즐겨찾기한 식사 조회
     */
    @Transactional(readOnly = true)
    public GetFavoriteFoodResponse getFavoriteFoodList(Long memberId){
        log.info("[FavoriteFoodService.getFavoriteFoodList]");

        if(memberRepository.findById(memberId).isEmpty()){
            throw new MemberException(MEMBER_NOT_FOUND);
        }
        Optional<List<FavoriteFood>> favoriteFoodList = favoriteFoodRepository.findByMember_MemberIdAndStatus(memberId,BaseStatus.A);
        if(favoriteFoodList.isEmpty()){
            return new GetFavoriteFoodResponse(new ArrayList<>()); // 비어 있는 리스트로 응답
        }

        List<GetFavoriteFoodResponse.FoodItem> foodList = favoriteFoodList.get().stream().map(favoriteFood -> {
            GetFavoriteFoodResponse.FoodItem foodDto = new GetFavoriteFoodResponse.FoodItem(
                    favoriteFood.getFood().getFoodId(),
                    favoriteFood.getFood().getName(),
                    (int)favoriteFood.getFood().getKcal()
            );
            return foodDto;
        }).collect(Collectors.toList());

        return new GetFavoriteFoodResponse(foodList);
    }

}