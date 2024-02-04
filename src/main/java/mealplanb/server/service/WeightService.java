package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.domain.Weight;
import mealplanb.server.dto.weight.GetWeightStatisticResponse.DailyWeightResponse;
import mealplanb.server.dto.weight.WeightResponse;
import mealplanb.server.repository.MemberRepository;
import mealplanb.server.repository.WeightRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
     * 체중 일간 조회
     */
    public DailyWeightResponse getDailyWeight(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));
        Optional<List<Weight>> dailyWeightsOptional = weightRepository.findAllByMemberAndStatusOrderByWeightDate(member, BaseStatus.A);
        List<WeightResponse> dailyWeights = makeDailyWeights(dailyWeightsOptional);
        return new DailyWeightResponse(dailyWeights);
    }

    private List<WeightResponse> makeDailyWeights(Optional<List<Weight>> dailyWeightsOptional) {
        /** Optional<List<Weight>> => List<WeightResponse> */
        log.info("[WeightService.makeDailyWeight]");
        List<Weight> dailyWeights = dailyWeightsOptional.orElse(Collections.emptyList());
        List<WeightResponse> weightResponseList = new ArrayList<>(); // 반환값

        for (Weight weight : dailyWeights){
            WeightResponse weightResponse = new WeightResponse(weight.getWeight(), weight.getWeightDate());
            weightResponseList.add(weightResponse);
        }
        return weightResponseList;
    }
}
