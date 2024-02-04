package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.dto.weight.WeightRequest;
import mealplanb.server.dto.weight.WeightResponse;
import mealplanb.server.service.WeightService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
     * 체중 등록
     */
    @PostMapping("")
    public BaseResponse<WeightResponse> postWeight(@Validated @RequestBody WeightRequest weightRequest,
                                                   @RequestHeader("Authorization") String authorization){
        log.info("[WeightController.postWeight]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(weightService.postWeight(memberId, weightRequest));
    }
}
