package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.dto.chat.GetAmountSuggestionResponse;
import mealplanb.server.dto.chat.GetCheatDayFoodResponse;
import mealplanb.server.dto.chat.GetCheatDayFoodResponse.cheatDayFoodInfo;
import mealplanb.server.dto.chat.GetFavoriteFoodResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final MemberService memberService;
    private final FoodService foodService;
    private final FoodMealMappingTableService foodMealMappingTableService;

    /**
     * 채팅(치팅데이)
     */
    public GetCheatDayFoodResponse getCheatDayFood(Long memberId, String category) {
        log.info("[ChatService.getCheatDayFood]");
        int remainingKcal = memberService.calculateRemainingKcal(memberId);
        String lackingNutrientName = memberService.getLackingNutrientName(memberId);
        List<cheatDayFoodInfo> cheatDayFood = foodService.getCheatDayFood(remainingKcal, lackingNutrientName, category);
        return new GetCheatDayFoodResponse(cheatDayFood);
    }

    /**
     * 채팅(자주먹는)
     */
    public GetFavoriteFoodResponse getMyFavoriteFood(Long memberId) {
        log.info("[ChatService.getMyFavoriteFood]");
        long myFavoriteFood = foodMealMappingTableService.getMyFavoriteFoodId(memberId);
        GetAmountSuggestionResponse amountSuggestion = getAmountSuggestion(memberId, myFavoriteFood);
        return new GetFavoriteFoodResponse(myFavoriteFood, amountSuggestion.getFoodName(), amountSuggestion.getOffer(), amountSuggestion.getOfferCarbohydrate(), amountSuggestion.getOfferProtein(), amountSuggestion.getOfferFat());
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
}
