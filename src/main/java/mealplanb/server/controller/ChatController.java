package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.dto.chat.GetAmountSuggestionResponse;
import mealplanb.server.dto.chat.GetCheatDayFoodResponse;
import mealplanb.server.service.ChatService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final JwtProvider jwtProvider;
    private final ChatService chatService;

    /**
     * 채팅(치팅데이)
     */
    @GetMapping("/cheat-day")
    public BaseResponse<GetCheatDayFoodResponse> getCheatDayFood(@RequestHeader("Authorization") String authorization,
                                                                 @RequestParam("category") String category){
        log.info("[ChatController.getCheatDayFood]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(chatService.getCheatDayFood(memberId, category));
    }

    /**
     * 얼마나 먹을까요
     */
    @GetMapping("/amount-suggestion/{foodId}")
    public BaseResponse<GetAmountSuggestionResponse> getAmountSuggestion(@RequestHeader("Authorization") String authorization,
                                                                         @PathVariable Long foodId){
        log.info("[ChatController.getAmountSuggestion]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(chatService.getAmountSuggestion(memberId, foodId));
    }
}