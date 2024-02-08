package mealplanb.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.WeightException;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.dto.GetKcalStatisticResponse;
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
            default:
                throw new WeightException(BaseExceptionResponseStatus.UNSUPPORTED_STATISTIC_TYPE);
        }

        return new BaseResponse<>(statisticResponse);
    }
}
