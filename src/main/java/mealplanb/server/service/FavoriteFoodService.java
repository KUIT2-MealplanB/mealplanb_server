package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FavoriteFood;
import mealplanb.server.domain.Food.Food;
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

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteFoodService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final FavoriteFoodRepository favoriteFoodRepository;
    private final FoodRepository foodRepository;

    /**
     * 즐겨찾기한 음식인지 여부 반환
     */
    public Boolean isFavorite(long memberId, long foodId){
        log.info("[FavoriteFoodService.isFavorite]");
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

        Food food = foodRepository.findByFoodIdAndStatus(foodId, BaseStatus.A)
                .orElseThrow(()-> new FoodException(FOOD_NOT_FOUND));

        if(favoriteFoodRepository.existsByMemberAndFoodAndStatus(member,food,BaseStatus.A)){
            throw new FoodException(FAVORITE_FOOD_ALREADY_EXIST);
        }

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
    public List<GetFavoriteFoodResponse> getFavoriteFoodList(Long memberId){
        log.info("[FavoriteFoodService.getFavoriteFoodList]");
        memberService.checkMemberExist(memberId);

        List<GetFavoriteFoodResponse> favoriteFoods = new ArrayList<>();
        Optional<List<FavoriteFood>>favoriteFoodListOptional = favoriteFoodRepository.findByMember_MemberIdAndStatus(memberId,BaseStatus.A);

        favoriteFoodListOptional.ifPresent(favoriteFoodList->{
                for(FavoriteFood food : favoriteFoodList) {
                    Long foodId = food.getFood().getFoodId();
                    String foodName = food.getFood().getName();
                    int kcal = (int) food.getFood().getKcal();

                    GetFavoriteFoodResponse foodItem = new GetFavoriteFoodResponse(
                            foodId,
                            foodName,
                            kcal
                    );

                    favoriteFoods.add(foodItem);
                }
            }
        );
        return favoriteFoods;
    }

    /**
     * 즐겨찾기 해제
     */
    @Transactional
    public void deleteFavoriteFood(Long memberId, Long foodId){
        log.info("[FavoriteFoodService.deleteFavoriteFood]");

        if(memberRepository.findById(memberId).isEmpty()){
            throw new MemberException(MEMBER_NOT_FOUND);
        }

        FavoriteFood favoriteFood = favoriteFoodRepository.findByMember_MemberIdAndFood_FoodIdAndStatus(memberId,foodId,BaseStatus.A)
                .orElseThrow(()-> new FoodException(FAVORITE_FOOD_NOT_EXIST));

        favoriteFood.updateStatus(BaseStatus.D);
    }

}