package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Food;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.food.GetFoodResponse;
import mealplanb.server.dto.food.PostNewFoodRequest;
import mealplanb.server.dto.food.PostNewFoodResponse;
import mealplanb.server.repository.FoodRepository;
import mealplanb.server.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final FavoriteFoodService favoriteFoodService;
    private final MemberRepository memberRepository;

    public GetFoodResponse getFoodDetail(long memberId, long foodId) {
        System.out.println("[FoodService.getFoodDetail]");
        Food food = foodRepository.findByFoodIdAndStatus(foodId, BaseStatus.A)
                .orElseThrow(()-> new FoodException(BaseExceptionResponseStatus.FOOD_NOT_FOUND));
        return new GetFoodResponse(
                food.getFoodId(),
                food.getName(),
                food.getQuantity(),
                food.getKcal(),
                food.getCarbohydrate(),
                food.getProtein(),
                food.getFat(),
                favoriteFoodService.isFavorite(memberId, foodId)
        );
    }

    public PostNewFoodResponse postNewFood(Long memberId, PostNewFoodRequest postNewFoodRequest) {
        System.out.println("[FoodService.postNewFood]");
        Food newFood = new Food(memberId, postNewFoodRequest);
        foodRepository.save(newFood);
        //todo: 코드 이렇게 길게 안하는 방법은 없을지...
        //todo: 사실 PostNewFoodResponse랑 Food랑 status, updatedAt, createdAt 유무 차이라서 Food를 반환하는게 나을려나.
        return new PostNewFoodResponse(
                newFood.getFoodId(),
                newFood.getName(),
                newFood.getCategory(),
                newFood.getKeyNutrient(),
                newFood.getQuantity(),
                newFood.getKcal(),
                newFood.getCarbohydrate(),
                newFood.getProtein(),
                newFood.getFat(),
                newFood.getSugar(),
                newFood.getSodium(),
                newFood.getCholesterol(),
                newFood.getSaturatedFattyAcid(),
                newFood.getTransFatAcid()
        );
    }

    @Transactional
    public void deleteUserCreatedFood(Long memberId, long foodId) {
        log.info("[FoodService.getFoodDetail]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));
        Food food = foodRepository.findById(foodId)
                .orElseThrow(()-> new FoodException(BaseExceptionResponseStatus.FOOD_NOT_FOUND));

        // 사용자가 식품을 지울 권한이 있는지 검증
        if (!memberId.equals(food.getCreateMemberId())){
            new FoodException(BaseExceptionResponseStatus.UNAUTHORIZED_ACCESS);
        }
        food.setStatus(BaseStatus.D);
    }
}