package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.common.exception.WeightException;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.domain.Weight;
import mealplanb.server.dto.weight.GetWeightStatisticResponse;
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
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.MEMBER_NOT_FOUND;
import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.WEIGHT_NOT_FOUND;

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

        LocalDate startDate = member.getCreatedAt().toLocalDate();
        LocalDate endDate = LocalDate.now();

        List<WeightResponse> result = createDailyEmptyWeightResponses(startDate, endDate);
        weightRepository.findAllByMemberAndStatusOrderByWeightDate(member, BaseStatus.A)
                .ifPresent(weights -> updateDailyWeightResponses(result, weights));

        return new WeightStatisticResponse("daily", result);
    }

    private List<WeightResponse> createDailyEmptyWeightResponses(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> new WeightResponse(0.0, date))
                .collect(Collectors.toList());
    }

    private void updateDailyWeightResponses(List<WeightResponse> result, List<Weight> weights) {
        weights.forEach(weight -> {
            int index = (int) ChronoUnit.DAYS.between(result.get(0).getDate(), weight.getWeightDate());
            if (index >= 0 && index < result.size()) {
                result.set(index, new WeightResponse(weight.getWeight(), weight.getWeightDate()));
            }
        });
    }

    /**
     * 체중 주간 조회
     */
    public WeightStatisticResponse getWeeklyWeight(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        LocalDate startDate = member.getCreatedAt().toLocalDate();
        LocalDate endDate = LocalDate.now();

        List<WeeklyWeight> result = createWeeklyEmptyWeightResponses(startDate, endDate);
        weightRepository.findWeeklyWeights(member.getMemberId(), BaseStatus.A)
                .ifPresent(weights -> updateWeeklyWeightResponses(result, weights)
                         );

        return new WeightStatisticResponse("weekly", result);
    }

    private List<WeeklyWeight> createWeeklyEmptyWeightResponses(LocalDate startDay, LocalDate endDay) {
        List<WeeklyWeight> weeklyWeights = new ArrayList<>();

        // 주의 시작을 월요일로 설정
        LocalDate monday = startDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        while (monday.isBefore(endDay) || monday.isEqual(endDay)) {
            // 주의 첫 날과 마지막 날 계산
            LocalDate weekStartDate = monday;
            LocalDate weekEndDate = monday.plusDays(6);

            WeeklyWeight weeklyWeight = new WeeklyWeight(0.0, weekStartDate, weekEndDate);
            weeklyWeights.add(weeklyWeight);

            // 다음 주의 월요일로 이동
            monday = monday.plusWeeks(1);
        }

        return weeklyWeights;
    }

    private void updateWeeklyWeightResponses(List<WeeklyWeight> result, List<WeeklyWeightNativeVo> weights) {
        weights.forEach(weight -> {
            log.info("week_average_weight = {}, week_start_date = {}, week_end_date = {}", weight.getWeekAverageWeight(), weight.getWeekStartDate(), weight.getWeekEndDate());
            int index = (int) ChronoUnit.WEEKS.between(result.get(0).getWeekStartDate(), LocalDate.parse(weight.getWeekStartDate()));
            if (index >= 0 && index < result.size()) {
                result.set(index, new WeeklyWeight(weight.getWeekAverageWeight(), LocalDate.parse(weight.getWeekStartDate()), LocalDate.parse(weight.getWeekEndDate())));
            }
        });
    }
}
