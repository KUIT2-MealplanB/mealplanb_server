package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.BadRequestException;
import mealplanb.server.common.exception.WeightException;
import mealplanb.server.common.response.BaseErrorResponse;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.dto.weight.WeightRequest;
import mealplanb.server.dto.weight.GetWeightStatisticResponse;
import mealplanb.server.dto.weight.GetWeightStatisticResponse.DailyWeightResponse;
import mealplanb.server.dto.weight.WeightResponse;
import mealplanb.server.service.WeightService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    /**
     * 체중 수정
     */
    @PatchMapping("")
    public BaseResponse<WeightResponse> modifyWeight(@Validated @RequestBody WeightRequest weightRequest,
                                                     @RequestHeader("Authorization") String authorization){
        log.info("[WeightController.modifyWeight]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(weightService.modifyWeight(memberId, weightRequest));
    }

    /**
     * 체중 일간, 주간, 월간 조회
     */
    @GetMapping("/{statisticType}")
    public BaseResponse<DailyWeightResponse> getDailyWeight(@RequestHeader("Authorization") String authorization,
                                                            @RequestParam(name = "statisticType") String statisticType) {
        log.info("[WeightController.getDailyWeight]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);

        switch (statisticType.toLowerCase()) {
            case "daily":
                return new BaseResponse<>(weightService.getDailyWeight(memberId));
            case "weekly":
                return new BaseResponse<>(weightService.getWeeklyWeight(memberId));
            case "monthly":
                return new BaseResponse<>(weightService.getMonthlyWeight(memberId));
            default:
                // 지원하지 않는 통계 타입인 경우 예외처리
                throw new WeightException(BaseExceptionResponseStatus.UNSUPPORTED_STATISTIC_TYPE);
        }
    }
}
