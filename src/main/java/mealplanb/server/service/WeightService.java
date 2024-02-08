package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.common.exception.WeightException;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.domain.Weight;
import mealplanb.server.dto.weight.GetWeightStatisticResponse.WeeklyWeight;
import mealplanb.server.dto.weight.GetWeightStatisticResponse.WeightStatisticResponse;
import mealplanb.server.dto.weight.WeightRequest;
import mealplanb.server.dto.weight.WeightResponse;
import mealplanb.server.repository.MemberRepository;
import mealplanb.server.repository.WeightRepository;
import mealplanb.server.repository.WeightRepository.WeeklyWeightNativeVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.MEMBER_NOT_FOUND;
import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.WEIGHT_NOT_FOUND;
import static mealplanb.server.dto.weight.GetWeightStatisticResponse.*;
import static mealplanb.server.repository.WeightRepository.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeightService {

    private final WeightRepository weightRepository;
    private final MemberRepository memberRepository;

    /**
     * 체중 조회
     */
    public WeightResponse getTodayWeight(Long memberId){
        Optional<Weight> weight = weightRepository.findTopByMember_MemberIdOrderByWeightDateDesc(memberId);

        // 가장 최근 몸무게가 존재하는 경우 그 값을 가져오고, 그렇지 않은 경우 member의 초기 몸무게를 가져옴
        double recentWeight = weight.map(Weight::getWeight).orElseGet(() -> {
            Optional<Member> member = memberRepository.findById(memberId);
            return member.get().getInitialWeight();
        });

        // 가장 최근 몸무게의 날짜 가져오고 그렇지 않은 경우 member 의 target_updated_at 날짜를 가져옴
        LocalDate date = weight.map(Weight::getWeightDate).orElseGet(()->{
            Optional<Member> member = memberRepository.findById(memberId);
            return member.get().getTargetUpdatedAt();
        });

        return new WeightResponse(recentWeight, date);
    }

    /**
     * 체중 등록
     */
    public WeightResponse postWeight(Long memberId, WeightRequest weightRequest){
        log.info("[WeightService.postWeight]");

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        // 요청받은 날짜에 해당 몸무게 저장
        double todayWeight = weightRequest.getWeight();
        LocalDate date = weightRequest.getDate();

        Weight weight = Weight.builder()
                .member(member)
                .weight(todayWeight)
                .weightDate(date)
                .status(BaseStatus.A)
                .build();

        weightRepository.save(weight);

        return new WeightResponse(weight.getWeight(), weight.getWeightDate());
    }

    /**
     * 체중 수정
     */
    @Transactional
    public WeightResponse modifyWeight(Long memberId, WeightRequest weightRequest){
        log.info("[WeightService.modifyWeight]");

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        double todayWeight = weightRequest.getWeight();
        LocalDate date = weightRequest.getDate();

        Weight weight = weightRepository.findByMemberAndWeightDate(member,date)
                .orElseThrow(()-> new WeightException(WEIGHT_NOT_FOUND));

        Weight updatedWeight = weight.toBuilder()
                .weight(todayWeight)
                .weightDate(date)
                .build();

        weight.updateFrom(updatedWeight);

        return new WeightResponse(weight.getWeight(), weight.getWeightDate());
    }

    /**
     * 체중 일간 조회
     */
    public WeightStatisticResponse getDailyWeight(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        List<Weight> weights = weightRepository.findAllByMemberAndStatusOrderByWeightDate(member, BaseStatus.A)
                .orElse(Collections.emptyList());

        List<WeightResponse> result = new ArrayList<>(); // 리턴할 결과
        LocalDate startDate = member.getCreatedAt().toLocalDate(); // 시작일자
        LocalDate endDate = LocalDate.now(); // 종료일자
        double initialWeight = member.getInitialWeight(); // 초기값: 초기체중

        makeDailyWeightList(result, weights, startDate, endDate, initialWeight);

        return new WeightStatisticResponse("daily", result);
    }

    private void makeDailyWeightList(List<WeightResponse> result, List<Weight> weights, LocalDate date, LocalDate endDate, double weightValue) {
        int index = 0;
        LocalDate dataWeightDate = weights.get(index).getWeightDate();

        // startDate부터 endDate까지 각 날짜에 대해 처리
        while (!date.isAfter(endDate)) {

            if (date.equals(dataWeightDate)) { // 데이터에 도달하면, 다음 데이터로 대체
                weightValue = weights.get(index).getWeight();
                if(index + 1 < weights.size()){
                    index += 1;
                    dataWeightDate = weights.get(index).getWeightDate();
                }
            }

            // 해당 날짜의 체중을 DailyWeightEntry로 추가.
            result.add(new WeightResponse(weightValue, date));

            // 다음 날짜로 이동.
            date = date.plusDays(1);
        }
    }

    /**
     * 체중 주간 조회
     */
    public WeightStatisticResponse getWeeklyWeight(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        List<WeeklyWeightNativeVo> weights = weightRepository.findWeeklyWeights(member.getMemberId(), BaseStatus.A)
                .orElse(Collections.emptyList());

        List<WeeklyWeight> result = new ArrayList<>(); //리턴할 결과
        LocalDate startDate = member.getCreatedAt().toLocalDate(); //시작일자
        LocalDate endDate = LocalDate.now(); //종료일자
        double initialWeight = member.getInitialWeight(); // 초기값: 초기체중

        makeWeeklyWeightList(result, weights, startDate, endDate, initialWeight);

        return new WeightStatisticResponse("weekly", result);
    }

    private void makeWeeklyWeightList(List<WeeklyWeight> result, List<WeeklyWeightNativeVo> weights, LocalDate date, LocalDate endDate, double weightValue) {
        int index = 0;
        LocalDate dataWeightWeekStartDate = LocalDate.parse(weights.get(index).getWeekStartDate());

        // startDate부터 endDate까지 각 날짜에 대해 처리
        while (!date.isAfter(endDate)) {

            if (date.equals(dataWeightWeekStartDate)) { // 데이터에 도달하면, 다음 데이터로 대체
                weightValue = weights.get(index).getWeekAverageWeight();
                if(index + 1 < weights.size()){
                    index += 1;
                    dataWeightWeekStartDate = LocalDate.parse(weights.get(index).getWeekStartDate());
                }
            }

            // 해당 날짜의 체중을 DailyWeightEntry로 추가.
            result.add(new WeeklyWeight(weightValue, date, date.plusDays(6)));

            // 다음 주로 이동.
            date = date.plusWeeks(1);
        }
    }

    /**
     * 체중 월간 조회
     */
    public WeightStatisticResponse getMonthlyWeight(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        List<MonthlyWeightNativeVo> weights = weightRepository.findMonthlyWeights(member.getMemberId(), BaseStatus.A)
                .orElse(Collections.emptyList());

        List<MonthlyWeight> result = new ArrayList<>(); //리턴할 결과
        LocalDate startDate = member.getCreatedAt().toLocalDate(); //시작일자
        LocalDate endDate = LocalDate.now(); //종료일자
        double initialWeight = member.getInitialWeight(); // 초기값: 초기체중

        makeMonthlyWeightList(result, weights, startDate, endDate, initialWeight);

        return new WeightStatisticResponse("monthly", result);
    }

    private void makeMonthlyWeightList(List<MonthlyWeight> result, List<MonthlyWeightNativeVo> weights, LocalDate startDate, LocalDate endDate, double weightValue) {
        int index = 0;

        // startDate의 년도와 월 정보 가져오기
        YearMonth currentMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);
        YearMonth dataWeightMonth = YearMonth.parse(weights.get(index).getMonth());

        // startDate부터 endDate까지 각 월에 대해 처리
        while (!currentMonth.isAfter(endMonth)) {

            //log.info("currentMonth = {} , endMonth = {}", currentMonth, endMonth);
            if (currentMonth.equals(dataWeightMonth)) { // 데이터에 도달하면, 다음 데이터로 대체
                weightValue = weights.get(index).getMonthAverageWeight();
                if(index + 1 < weights.size()){
                    index += 1;
                    dataWeightMonth = YearMonth.parse(weights.get(index).getMonth());
                }
            }

            // 해당 날짜의 체중을 DailyWeightEntry로 추가.
            result.add(new MonthlyWeight(weightValue, currentMonth));

            // 다음 달로 이동
            currentMonth = currentMonth.plusMonths(1);
        }
    }
}
