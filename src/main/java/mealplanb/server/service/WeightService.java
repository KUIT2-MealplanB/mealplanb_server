package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.exception.WeightException;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.domain.Member.MemberStatus;
import mealplanb.server.domain.Weight;
import mealplanb.server.dto.weight.WeightRequest;
import mealplanb.server.dto.weight.WeightResponse;
import mealplanb.server.repository.MemberRepository;
import mealplanb.server.repository.WeightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

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
}
