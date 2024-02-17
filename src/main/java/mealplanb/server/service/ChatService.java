package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.ChatException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.dto.chat.GetAmountSuggestionResponse;
import mealplanb.server.dto.chat.GetCheatDayFoodResponse;
import mealplanb.server.dto.chat.GetCheatDayFoodResponse.cheatDayFoodInfo;
import mealplanb.server.dto.chat.GetFavoriteFoodResponse;
import mealplanb.server.dto.meal.PostMealFoodRequest.FoodItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final MemberService memberService;
    private final FoodService foodService;
    private final FoodMealMappingTableService foodMealMappingTableService;
    private final MealService mealService;

    /**
     * 채팅(치팅데이)
     */
    public GetCheatDayFoodResponse getCheatDayFood(Long memberId, String category) {
        log.info("[ChatService.getCheatDayFood]");
        Map<String, Object> result = memberService.calculateRemainingKcalAndLackingNutrientName(memberId);
        int remainingKcal = (int) result.get("remainingKcal");
        if (remainingKcal <= 0){
            throw new ChatException(BaseExceptionResponseStatus.CHAT_CHEAT_DAY_LEFT_KCAL_NOT_EXIST);
        }

        String lackingNutrient1 = (String) result.get("lackingNutrient1");
        String lackingNutrient2 = (String) result.get("lackingNutrient2");
        String lackingNutrient3 = (String) result.get("lackingNutrient3");
        List<cheatDayFoodInfo> cheatDayFood = foodService.getCheatDayFood(remainingKcal, lackingNutrient1, lackingNutrient2, lackingNutrient3, category);
        return new GetCheatDayFoodResponse(cheatDayFood);
    }

    /**
     * 채팅(자주먹는)
     */
    public GetFavoriteFoodResponse getMyFavoriteFood(Long memberId) {
        log.info("[ChatService.getMyFavoriteFood]");
        long myFavoriteFoodId = foodMealMappingTableService.getMyFavoriteFoodId(memberId);
        GetAmountSuggestionResponse amountSuggestion = getAmountSuggestion(memberId, myFavoriteFoodId);
        return new GetFavoriteFoodResponse(myFavoriteFoodId, amountSuggestion.getFoodName(), amountSuggestion.getOffer(), amountSuggestion.getOfferCarbohydrate(), amountSuggestion.getOfferProtein(), amountSuggestion.getOfferFat());
    }

    /**
     * 채팅(인기있는)
     */
    public GetFavoriteFoodResponse getCommunityFavoriteFood(Long memberId) {
        log.info("[ChatService.getCommunityFavoriteFood]");
        memberService.checkMemberExist(memberId);
        long communityFavoriteFoodId = foodMealMappingTableService.getCommunityFavoriteFoodId();
        GetAmountSuggestionResponse amountSuggestion = getAmountSuggestion(memberId, communityFavoriteFoodId);
        return new GetFavoriteFoodResponse(communityFavoriteFoodId, amountSuggestion.getFoodName(), amountSuggestion.getOffer(), amountSuggestion.getOfferCarbohydrate(), amountSuggestion.getOfferProtein(), amountSuggestion.getOfferFat());
    }

    /**
     * 얼마나 먹을까요
     */
    public GetAmountSuggestionResponse getAmountSuggestion(Long memberId, Long foodId) {
        log.info("[ChatService.getAmountSuggestion]");
        int remainingKcal = memberService.calculateRemainingKcal(memberId);
        return foodService.getAmountSuggestion(remainingKcal, foodId);
    }

    /**
     * 채팅을 통한 끼니 등록
     */
    public void postMealSuggestedFood(Long memberId, FoodItem food) {
        log.info("[ChatService.postMealSuggestedFood]");
        mealService.postMealSuggestedFood(memberId, food);
    }
}
