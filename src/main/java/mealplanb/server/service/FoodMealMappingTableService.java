package mealplanb.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MealException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Food.Food;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.FoodMealMappingTable;
import mealplanb.server.dto.chat.GetFavoriteFoodResponse;
import mealplanb.server.dto.chat.GetMealSuggestedFoodResponse;
import mealplanb.server.dto.meal.GetMealFoodResponse.FoodInfo;
import mealplanb.server.domain.Meal.Meal;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.meal.MealTypeConverter;
import mealplanb.server.repository.FoodMealMappingTableRepository;
import mealplanb.server.repository.FoodRepository;
import mealplanb.server.repository.MealRepository;
import mealplanb.server.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import static mealplanb.server.dto.meal.PostMealFoodRequest.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodMealMappingTableService {
    private final MemberRepository memberRepository;
    private final FoodMealMappingTableRepository foodMealMappingTableRepository;
    private final FoodRepository foodRepository;
    private final MealRepository mealRepository;

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
    public long getMyFavoriteFoodId(Long memberId) {
        log.info("[FoodMealMappingTableService.getMyFavoriteFood]");
        Long mostEatenFoodId = foodMealMappingTableRepository.findMostEatenFoodIdByUserId(memberId);
        Food myFavoriteFood = foodRepository.findByFoodIdAndStatus(mostEatenFoodId, BaseStatus.A)
                .orElseThrow(() -> new FoodException(BaseExceptionResponseStatus.FOOD_NOT_FOUND));
        return myFavoriteFood.getFoodId();
    }

    /**
     * 채팅(인기있는)
     */
    public long getCommunityFavoriteFoodId() {
        log.info("[FoodMealMappingTableService.getCommunityFavoriteFood]");
        Long mostEatenFoodId = foodMealMappingTableRepository.findMostEatenFoodId();
        Food communityFavoriteFood = foodRepository.findByFoodIdAndStatus(mostEatenFoodId, BaseStatus.A)
                .orElseThrow(() -> new FoodException(BaseExceptionResponseStatus.FOOD_NOT_FOUND));

        return communityFavoriteFood.getFoodId();
    }

    public boolean isMealEmpty(Long mealId) {
        Optional<List<FoodMealMappingTable>> mealFoodList= foodMealMappingTableRepository.findAllByMeal_MealIdAndStatus(mealId, BaseStatus.A);
        log.info("[FoodMealMappingTableService.isMealEmpty] mealId {} isMealEmpty = {}", mealId, mealFoodList.get().isEmpty());
        return mealFoodList.get().isEmpty();
    }

    /**
     * 채팅을 통해 추천받은 끼니 조회 (등록식단 모아보기)
     */
    public List<GetMealSuggestedFoodResponse> getMealSuggestedFood(Long memberId) {
        log.info("[MealService.getMealSuggestedFood]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        List<FoodMealMappingTable> suggestedFoodList = foodMealMappingTableRepository.findAllMealSuggestedFood(memberId)
                .orElse(Collections.emptyList());

        List<GetMealSuggestedFoodResponse> responseList = new ArrayList<>();
        for (FoodMealMappingTable suggestedFood: suggestedFoodList){
            Meal meal = mealRepository.findByMealIdAndStatus(suggestedFood.getMeal().getMealId(), BaseStatus.A)
                    .orElseThrow(()-> new MealException(BaseExceptionResponseStatus.MEAL_NOT_FOUND));

            String date = meal.getMealDate() + " " + convertMealDateLabel(meal.getMealDate().getDayOfWeek());
            String mealType = MealTypeConverter.convertMealTypeLabel(meal.getMealType());

            String name = suggestedFood.getFood().getName();
            int quantity = suggestedFood.getQuantity();
            int offerCarbohydrate = (int) (suggestedFood.getFood().getCarbohydrate() * quantity);
            int offerProtein = (int) (suggestedFood.getFood().getProtein() * quantity);
            int offerFat = (int) (suggestedFood.getFood().getFat() * quantity);

            GetMealSuggestedFoodResponse response = new GetMealSuggestedFoodResponse(date, mealType, name, offerCarbohydrate, offerProtein, offerFat);
            responseList.add(response);
        }

        return responseList;
    }

    public static String convertMealDateLabel(DayOfWeek dayOfWeek) {
        // 요일에 따라 한글 요일을 반환
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN);
    }
}
