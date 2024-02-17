package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Food;
import mealplanb.server.dto.food.*;
import mealplanb.server.dto.food.GetFavoriteFoodResponse.FoodItem;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.food.GetFoodResponse;
import mealplanb.server.dto.food.PostNewFoodRequest;
import mealplanb.server.dto.food.PostNewFoodResponse;
import mealplanb.server.repository.FoodRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import mealplanb.server.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final FavoriteFoodService favoriteFoodService;
    private final MemberRepository memberRepository;

    /**
     * 특정 식사의 상세 정보 반환
     */
    public GetFoodResponse getFoodDetail(long memberId, long foodId) {
        log.info("[FoodService.getFoodDetail]");
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

    /**
     * 식사 등록 by 사용자
     */
    public PostNewFoodResponse postNewFood(Long memberId, PostNewFoodRequest postNewFoodRequest) {
        log.info("[FoodService.postNewFood]");
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

    /**
     * 사용자 등록 식품(=식사) 삭제
     */
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

    /**
     * 자동완성 검색
     */
    public GetFoodAutoCompleteResponse getAutoComplete(Long memberId, String query, int page, int size) {
        log.info("[FoodService.GetFoodAutoCompleteResponse]");
        Pageable pageable = PageRequest.of(page, size);
        Page<Food> autoComplete = foodRepository.getAutoComplete(memberId, query.strip(), pageable);
        List<FoodItem> foods = autoComplete.getContent().stream()
                .map(FoodItem::new) // autoComplete.getContent()를 하면 List<Food>가 결과로 나오는데 각 요소인 Food를 FoodItem으로 변환
                .collect(Collectors.toList());
        return new GetFoodAutoCompleteResponse(page, autoComplete.getTotalPages(), foods);
    }
}