package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.domain.Weight;
import mealplanb.server.repository.WeightRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {

    private final WeightRepository weightRepository;
    /**
     * 아바타 외형 계산
     */
    public String calculateAvatarAppearance(Member member){

        // 체중이 입력되지 않았을 경우, member의 초기 체중을 사용
        double bodyWeight = weightRepository.findTopByMemberOrderByWeightDateDesc(member)
                .map(Weight::getWeight)
                .orElse(member.getInitialWeight());

        String gender = member.getSex().toString();
        int skeletalMuscleMass = member.getSkeletalMuscleMass();
        int bodyFatMass = member.getBodyFatMass();
        String appearance = "normal";

        if("M".equals(gender)){ // 남성인 경우
            if(((bodyWeight*0.45)*1.1 <= skeletalMuscleMass) && bodyWeight*0.2 >= bodyFatMass){
                // 골격근량 표준 이상 and 체지방량 표준이하 혹은 표준
                return appearance = "muscle";

            } else if (((bodyWeight*0.1 <= bodyFatMass) && (bodyWeight*0.2 >= bodyFatMass)) || bodyFatMass == 0 || skeletalMuscleMass == 0) {
                // 골격근량, 체지방량 입력 x or 체지방량 정상 범위
                return appearance = "normal";

            } else if(bodyWeight*0.2 < bodyFatMass){
                // 체지방량 표준 이상
                return appearance = "fat";
            }
        } else if ("F".equals(gender)) { // 여성인 경우
            if(((bodyWeight*0.45)*1.1<= skeletalMuscleMass) && bodyWeight*0.28 >= bodyFatMass){
                // 골격근량 평균 이상 and 체지방량 표준이하 혹은 표준
                return appearance = "muscle";

            } else if (((bodyWeight*0.18 <= bodyFatMass) && (bodyWeight*0.28 >= bodyFatMass)) || bodyFatMass == 0 || skeletalMuscleMass == 0) {
                // 골격근량, 체지방량 입력 x or 체지방량 정상 범위
                return appearance = "normal";

            } else if(bodyWeight*0.28 < bodyFatMass){
                // 체지방량 표준 이상
                return appearance = "fat";
            }
        }
        return appearance;
    }
}
