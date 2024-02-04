package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.dto.weight.GetWeightStatisticResponse;
import mealplanb.server.dto.weight.GetWeightStatisticResponse.DailyWeightResponse;
import mealplanb.server.dto.weight.WeightResponse;
import mealplanb.server.service.WeightService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/weight")
public class WeightController {

    private final JwtProvider jwtProvider;
    private final WeightService weightService;

    /**
     * 체중 조회
     */
    @GetMapping("")
    public BaseResponse<WeightResponse> getTodayWeight(@RequestHeader("Authorization") String authorization){
        log.info("[WeightController.getTodayWeight]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(weightService.getTodayWeight(memberId));
    }

    /**
     * 체중 일간 조회
     */
    @GetMapping("/daily")
    public BaseResponse<DailyWeightResponse> getDailyWeight(@RequestHeader("Authorization") String authorization){
        log.info("[WeightController.getDailyWeight]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(weightService.getDailyWeight(memberId));
    }
}
