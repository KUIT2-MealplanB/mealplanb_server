package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;

import mealplanb.server.domain.Member.Member;
import mealplanb.server.domain.Member.MemberSex;
import mealplanb.server.domain.Member.MemberStatus;
import mealplanb.server.dto.user.*;
import mealplanb.server.repository.MemberRepository;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AvatarService avatarService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 회원 가입
     */
    public PostMemberResponse signUp(PostMemberRequest postUserRequest){
        log.info("[MemberService.signUp]");
        validateEmail(postUserRequest.getEmail()); // 이메일 유효성 검사
        Member member = createAndSaveMember(postUserRequest);
        String jwt = generateJwtToken(postUserRequest.getEmail(), member.getMemberId());
        return new PostMemberResponse(member.getMemberId(), jwt);
    }

    private Member createAndSaveMember(PostMemberRequest postMemberRequest) {
        String email = postMemberRequest.getEmail();
        String encodedPassword = passwordEncoder.encode(postMemberRequest.getPassword());
        MemberSex memberSex = MemberSex.valueOf(postMemberRequest.getSex().toUpperCase());
        int age = postMemberRequest.getAge();
        int height = postMemberRequest.getHeight();
        double initialWeight = postMemberRequest.getInitialWeight();
        double targetWeight = postMemberRequest.getTargetWeight();
        int recommendedKcal = calRecommendedKcal(memberSex,age,height, initialWeight, targetWeight);
        int targetKcal = recommendedKcal;
        String dietType = postMemberRequest.getDietType();

        int[] ratio = calculateRate(dietType);
        int carbohydrateRate = ratio[0];
        int proteinRate = ratio[1];
        int fatRate = ratio[2];

        String avatarColor = postMemberRequest.getAvatarColor();
        String nickname = postMemberRequest.getNickname();
        int skeletalMuscleMass = 0;
        int bodyFatMass = 0;
        MemberStatus status = MemberStatus.A;

        Member member = new Member(
                email,
                encodedPassword,
                memberSex,
                age,
                height,
                initialWeight,
                targetWeight,
                recommendedKcal,// 추천 칼로리
                targetKcal,// 타켓 칼로리 -> default 추천칼로리
                dietType,
                carbohydrateRate,// 식단에 따른 탄수화물 비율
                proteinRate,// 식단 종류에 따른 단백질 비율
                fatRate,// 식단 종류에 따른 지방 비율
                avatarColor,
                nickname,
                skeletalMuscleMass,//  골격근량 -> default 0
                bodyFatMass,// 체지방량 -> default 0
                status
        );
        log.info("멤버 객체 생성 후: {}", member);
        return memberRepository.save(member);
    }

    private int[] calculateRate(String dietType) {
        int[] ratio = new int[3];
        switch (dietType) {
            case "일반", "비건" -> {
                ratio[0] = 5;
                ratio[1] = 3;
                ratio[2] = 2;
            }
            case "운동" -> {
                ratio[0] = 4;
                ratio[1] = 4;
                ratio[2] = 2;
            }
            case "키토" -> {
                ratio[0] = 8;
                ratio[1] = 22;
                ratio[2] = 70;
            }
            case "당뇨" -> {
                ratio[0] = 65;
                ratio[1] = 25;
                ratio[2] = 10;
            }
        }
        return ratio;
    }

    private int calRecommendedKcal(MemberSex sex, int age, int height, double initialWeight, double targetWeight) {
        // 기초 대사량 계산
        int BMR = calBMR(sex,age,height,initialWeight);
        log.info("[BMR 계산 결과 :{}] ",BMR);
        // 일일 칼로리 계산
        int dailyKcal = (int)(BMR * 1.5);
        log.info("[일일 칼로리 계산 결과 :{}] ",dailyKcal);
        // 일일 목표 칼로리 섭취량 계산
        int targetKcal = 0;
        if(initialWeight > targetWeight){ // 감량 하는 경우
            targetKcal = dailyKcal - 500;
            log.info("[감량 타켓 칼로리 계산 결과 :{}] ",targetKcal);
        } else if (initialWeight < targetWeight) { // 증량하는 경우
            targetKcal = dailyKcal + 500;
            log.info("[증량 타켓 칼로리 계산 결과 :{}] ",targetKcal);
        } else { // 같은 경우
            targetKcal = dailyKcal; // 현재 체중에 맞는 일일 칼로리 섭취량 추천
            log.info("[동일한경우 타켓 칼로리 계산 결과 :{}] ",targetKcal);
        }
        return targetKcal;
    }

    private int calBMR(MemberSex sex, int age, int height, double initialWeight) {
        int BMR = 0;
        if(sex == MemberSex.M){ // 남성인 경우
            BMR = (int)(88.362 + (13.397 * initialWeight) + (4.799 * height) - (5.677 * age));
            log.info("[남성 BMR : {}]", BMR);
        } else if (sex == MemberSex.F) { // 여성인 경우
            BMR = (int)(447.593 + (9.247 * initialWeight) + (3.098 * height) - (4.330 * age));
            log.info("[여성 BMR : {}]", BMR);
        }
        return BMR;
    }

    private void validateEmail(String email) {
        if(memberRepository.existsByEmail(email)){
            throw new MemberException(DUPLICATE_EMAIL);
        }
    }

    /**
     * 로그인
     */
    public PostLoginResponse login(PostLoginRequest postLoginRequest){
        log.info("[MemberService.login]");
        Long memberId = findMemberByEmail(postLoginRequest.getEmail());
        String encodedPassword = findEncodedPassword(memberId);

        if(passwordEncoder.matches(postLoginRequest.getPassword(), encodedPassword)) {
            String jwt = generateJwtToken(postLoginRequest.getEmail(), memberId);
            return new PostLoginResponse(memberId, jwt);
        }else {
            throw new MemberException(PASSWORD_NO_MATCH);
        }
    }

    private String findEncodedPassword(Long memberId) {
        Optional<Member> passwordOptional = memberRepository.findById(memberId);
        if(passwordOptional.isEmpty()){
            throw new MemberException(MEMBER_NOT_FOUND);
        }else {
            return passwordOptional.get().getPassword(); // 패스워드 값 추출
        }
    }

    private Long findMemberByEmail(String email) {
        Optional<Member> memberIdOptional = memberRepository.findByEmail(email);
        if(memberIdOptional.isEmpty()){
            throw new MemberException(EMAIL_NOT_FOUND);
        } else {
            return memberIdOptional.get().getMemberId(); // Id 값 추출
        }
    }

    private String generateJwtToken(String email, Long memberId){
        String jwt = jwtProvider.createToken(email, memberId);
        return jwt;
    }

    /** 아바타 정보 조회 */
    @Transactional(readOnly = true)
    public GetAvatarResponse getAvatarResponse(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));
        String avatarAppearance = avatarService.calculateAvatarAppearance(member);
        return new GetAvatarResponse(avatarAppearance, member.getAvatarColor(), member.getNickname());
    }

    /**
     * 아바타 외형 수정
     */
    public PatchAvatarAppearanceResponse modifyAvatarAppearance(Long memberId, PatchAvatarAppearanceRequest patchAvatarAppearanceRequest){
        log.info("[MemberService.modifyAvatarAppearance]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        int skeletalMuscleMass = patchAvatarAppearanceRequest.getSkeletalMuscleMass();
        int bodyFatMass = patchAvatarAppearanceRequest.getFatMass();

        member.setSkeletalMuscleMass(skeletalMuscleMass);
        member.setBodyFatMass(bodyFatMass);

        memberRepository.save(member);

        return new PatchAvatarAppearanceResponse(avatarService.calculateAvatarAppearance(member));
    }
}
