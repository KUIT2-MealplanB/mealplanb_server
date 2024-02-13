package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.dto.chat.GetAmountSuggestionResponse;
import mealplanb.server.dto.chat.GetCheatDayFoodResponse;
import mealplanb.server.dto.chat.GetCheatDayFoodResponse.cheatDayFoodInfo;
import mealplanb.server.dto.chat.GetMyFavoriteFoodResponse;
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
    public GetMyFavoriteFoodResponse getMyFavoriteFood(Long memberId) {
        log.info("[ChatService.getMyFavoriteFood]");
        return foodMealMappingTableService.getMyFavoriteFood(memberId);
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
