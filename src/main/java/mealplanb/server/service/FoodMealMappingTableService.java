package mealplanb.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Food.Food;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.FoodMealMappingTable;
import mealplanb.server.dto.chat.GetFavoriteFoodResponse;
import mealplanb.server.dto.meal.GetMealFoodResponse.FoodInfo;
import mealplanb.server.domain.Meal.Meal;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.repository.FoodMealMappingTableRepository;
import mealplanb.server.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static mealplanb.server.dto.meal.PostMealFoodRequest.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodMealMappingTableService {
    private final FoodMealMappingTableRepository foodMealMappingTableRepository;
    private final FoodRepository foodRepository;

    /**
     * 해당 끼니의 총 칼로리 반환
     */
    public double getMealKcal(Long mealId) {
        log.info("[FoodMealMappingTableService.getMealKcal]");
        Optional<List<FoodMealMappingTable>> foodsInMeal = foodMealMappingTableRepository.findAllByMeal_MealIdAndStatus(mealId, BaseStatus.A);

        double mealKcal = 0.0;
        if (foodsInMeal.isPresent()){
            for (FoodMealMappingTable item: foodsInMeal.get()){
                log.info("[FoodMealMappingTableService.getMealKcal] : mealId = {}, foodId = {}, foodKcalPer100 = {}, foodQauntity = {}", mealId, item.getFood().getFoodId(), item.getFood().getKcal(), item.getQuantity());
                double foodKcal = calculateFoodKcal(item.getQuantity(), item.getFood().getKcal());
                mealKcal += foodKcal;
            }
        }
        log.info("[FoodMealMappingTableService.getMealKcal] : mealId = {}, mealKcal = {}", mealId, mealKcal);
        return mealKcal;
    }

    /**
     * 끼니 삭제 시, 해당 끼니에 연결되어있는 식품 정보의 status -> D로 변경
     */
    @Transactional
    public void deleteFoodMealMapping(Long mealId){
        log.info("[FoodMealMappingTableService.deleteFoodMealMapping]");
        Optional<List<FoodMealMappingTable>> optionalFoodsInMeal = foodMealMappingTableRepository.findAllByMeal_MealIdAndStatus(mealId, BaseStatus.A);
        optionalFoodsInMeal.ifPresent(foodsInMeal -> {
            for (FoodMealMappingTable item: foodsInMeal){
                item.setStatus(BaseStatus.D);
            }
        });
    }

    /**
     * 해당 끼니에 식사 리스트 등록, 수정
     */
    @Transactional
    public void postMealFood(Member member, Meal meal, List<FoodItem> foods, boolean isRecommended) {
        log.info("[FoodMealMappingTableService.postMealFood]");
        deleteFoodMealMapping(meal.getMealId());

        for (FoodItem foodItem : foods){
            Food food = foodRepository.findById(foodItem.getFoodId())
                    .orElseThrow(() -> new FoodException(BaseExceptionResponseStatus.FOOD_NOT_FOUND));
            FoodMealMappingTable foodMealMappingTable = new FoodMealMappingTable(member, meal, food, foodItem.getQuantity(), isRecommended);
            foodMealMappingTableRepository.save(foodMealMappingTable);
        }
    }

    /**
     * 끼니의 식사 리스트 조회
     */
    public List<FoodInfo> getMealFoodList(long mealId) {
        log.info("[FoodMealMappingTableService.getMealFoodList]");
        Optional<List<FoodMealMappingTable>> mealFoodList= foodMealMappingTableRepository.findAllByMeal_MealIdAndStatus(mealId, BaseStatus.A);
        return makeFoodInfoList(mealFoodList);
    }

    private List<FoodInfo> makeFoodInfoList(Optional<List<FoodMealMappingTable>> foodMealMappingTables) {
        log.info("[FoodMealMappingTableService.makeFoodInfoList]");
        List<FoodInfo> foodInfoList = new ArrayList<>();

        for (FoodMealMappingTable i: foodMealMappingTables.get()){
            Food food = i.getFood();
            FoodInfo foodInfo = new FoodInfo(food.getFoodId(), i.getQuantity(), food.getName(), calculateFoodKcal(i.getQuantity(), food.getKcal()));
            foodInfoList.add(foodInfo);
        }
        return foodInfoList;
    }

    private int calculateFoodKcal(int quantity, double kcalPer100){
        double kcalPerGram = kcalPer100/100;
        return (int) (quantity * kcalPerGram);
    }

    /**
     * 채팅(자주먹는)
     */
    public GetFavoriteFoodResponse getMyFavoriteFood(Long memberId) {
        log.info("[FoodMealMappingTableService.getMyFavoriteFood]");
        Long mostEatenFoodId = foodMealMappingTableRepository.findMostEatenFoodIdByUserId(memberId);
        Food myFavoriteFood = foodRepository.findByFoodIdAndStatus(mostEatenFoodId, BaseStatus.A)
                .orElseThrow(() -> new FoodException(BaseExceptionResponseStatus.FOOD_NOT_FOUND));
        return new GetFavoriteFoodResponse(myFavoriteFood.getFoodId(), myFavoriteFood.getName(), (int) myFavoriteFood.getCarbohydrate(), (int) myFavoriteFood.getProtein(), (int) myFavoriteFood.getFat());
    }

    /**
     * 채팅(인기있는)
     */
    public GetFavoriteFoodResponse getCommunityFavoriteFood() {
        log.info("[FoodMealMappingTableService.getCommunityFavoriteFood]");
        Long mostEatenFoodId = foodMealMappingTableRepository.findMostEatenFoodId();
        Food communityFavoriteFood = foodRepository.findByFoodIdAndStatus(mostEatenFoodId, BaseStatus.A)
                .orElseThrow(() -> new FoodException(BaseExceptionResponseStatus.FOOD_NOT_FOUND));
        return new GetFavoriteFoodResponse(communityFavoriteFood.getFoodId(), communityFavoriteFood.getName(), (int) communityFavoriteFood.getCarbohydrate(), (int) communityFavoriteFood.getProtein(), (int) communityFavoriteFood.getFat());
    }

    public boolean isMealEmpty(Long mealId) {
        Optional<List<FoodMealMappingTable>> mealFoodList= foodMealMappingTableRepository.findAllByMeal_MealIdAndStatus(mealId, BaseStatus.A);
        log.info("[FoodMealMappingTableService.isMealEmpty] mealId {} isMealEmpty = {}", mealId, mealFoodList.get().isEmpty());
        return mealFoodList.get().isEmpty();
    }
}
