package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.WeightException;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.dto.statistic.GetKcalStatisticResponse;
import mealplanb.server.dto.statistic.GetStatisticPlanResponse;
import mealplanb.server.service.StatisticService;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatisticController {

    private final JwtProvider jwtProvider;
    private final StatisticService statisticService;

    /**
     * 칼로리 & 탄단지 일간, 주간, 월간 조회
     */
    @GetMapping("food-history/{statisticType}")
    public BaseResponse<GetKcalStatisticResponse> getKcalStatistic(@RequestHeader("Authorization") String authorization,
                                                      @PathVariable String statisticType) {
        log.info("[StatisticController.getKcalStatistic]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);

        GetKcalStatisticResponse statisticResponse;

        switch (statisticType) {
            case "daily":
                statisticResponse = statisticService.getDailyKcals(memberId);
                break;
            case "weekly":
                statisticResponse = statisticService.getWeeklyKcals(memberId);
                break;
            case "monthly":
                statisticResponse = statisticService.getMonthlyKcals(memberId);
                break;
            default:
                throw new WeightException(BaseExceptionResponseStatus.UNSUPPORTED_STATISTIC_TYPE);
        }

        return new BaseResponse<>(statisticResponse);
    }

    /**
     * 통계페이지 상단 목표 조회
     */
    @GetMapping("statistic/plan")
    public BaseResponse<GetStatisticPlanResponse> getStatisticPlan(@RequestHeader("Authorization") String authorization) {
        log.info("[StatisticController.getStatisticPlan]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(statisticService.getStatisticPlan(memberId));

    }
}
