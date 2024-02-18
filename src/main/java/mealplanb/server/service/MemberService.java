package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;

import mealplanb.server.domain.Member.Member;
import mealplanb.server.domain.Member.MemberSex;
import mealplanb.server.domain.Member.MemberStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Food.Food;
import mealplanb.server.domain.FoodMealMappingTable;
import mealplanb.server.domain.Meal.Meal;
import mealplanb.server.dto.member.*;
import mealplanb.server.repository.FoodRepository;
import mealplanb.server.repository.MealRepository;
import mealplanb.server.repository.MemberRepository;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MealRepository mealRepository;
    private final FoodRepository foodRepository;
    private final AvatarService avatarService;
    private final FoodMealMappingTableService foodMealMappingTableService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 회원 가입
     */
    public PostMemberResponse signUp(PostMemberRequest postUserRequest) {
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
        int recommendedKcal = calRecommendedKcal(memberSex, age, height, initialWeight, targetWeight);
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
                ratio[0] = 50;
                ratio[1] = 30;
                ratio[2] = 20;
            }
            case "운동" -> {
                ratio[0] = 40;
                ratio[1] = 40;
                ratio[2] = 20;
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
        int BMR = calBMR(sex, age, height, initialWeight);
        //log.info("[BMR 계산 결과 :{}] ", BMR);

        // 일일 칼로리 계산
        int dailyKcal = (int) (BMR * 1.5);
        //log.info("[일일 칼로리 계산 결과 :{}] ", dailyKcal);

        // 일일 목표 칼로리 섭취량 계산
        int targetKcal = 0;
        if (initialWeight > targetWeight) { // 감량 하는 경우
            targetKcal = dailyKcal - 500;
            //log.info("[감량 타켓 칼로리 계산 결과 :{}] ", targetKcal);
        } else if (initialWeight < targetWeight) { // 증량하는 경우
            targetKcal = dailyKcal + 500;
            //log.info("[증량 타켓 칼로리 계산 결과 :{}] ", targetKcal);
        } else { // 같은 경우
            targetKcal = dailyKcal; // 현재 체중에 맞는 일일 칼로리 섭취량 추천
            //log.info("[동일한경우 타켓 칼로리 계산 결과 :{}] ", targetKcal);
        }
        return targetKcal;
    }

    private int calBMR(MemberSex sex, int age, int height, double initialWeight) {
        int BMR = 0;
        if (sex == MemberSex.M) { // 남성인 경우
            BMR = (int) (88.362 + (13.397 * initialWeight) + (4.799 * height) - (5.677 * age));
            log.info("[남성 BMR : {}]", BMR);
        } else if (sex == MemberSex.F) { // 여성인 경우
            BMR = (int) (447.593 + (9.247 * initialWeight) + (3.098 * height) - (4.330 * age));
            log.info("[여성 BMR : {}]", BMR);
        }
        return BMR;
    }

    private void validateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(DUPLICATE_EMAIL);
        }
    }

    /**
     * 로그인
     */
    public PostLoginResponse login(PostLoginRequest postLoginRequest) {
        log.info("[MemberService.login]");
        Long memberId = findMemberByEmail(postLoginRequest.getEmail());
        String encodedPassword = findEncodedPassword(memberId);

        if (passwordEncoder.matches(postLoginRequest.getPassword(), encodedPassword)) {
            String jwt = generateJwtToken(postLoginRequest.getEmail(), memberId);
            return new PostLoginResponse(memberId, jwt);
        } else {
            throw new MemberException(PASSWORD_NO_MATCH);
        }
    }

    private String findEncodedPassword(Long memberId) {
        Optional<Member> passwordOptional = memberRepository.findById(memberId);
        if (passwordOptional.isEmpty()) {
            throw new MemberException(MEMBER_NOT_FOUND);
        } else {
            return passwordOptional.get().getPassword(); // 패스워드 값 추출
        }
    }

    private Long findMemberByEmail(String email) {
        Optional<Member> memberIdOptional = memberRepository.findByEmail(email);
        if (memberIdOptional.isEmpty()) {
            throw new MemberException(EMAIL_NOT_FOUND);
        } else {
            return memberIdOptional.get().getMemberId(); // Id 값 추출
        }
    }

    private String generateJwtToken(String email, Long memberId) {
        String jwt = jwtProvider.createToken(email, memberId);
        return jwt;
    }

    /**
     * 아바타 정보 조회
     */
    @Transactional(readOnly = true)
    public GetAvatarResponse getAvatarResponse(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));
        String avatarAppearance = avatarService.calculateAvatarAppearance(member);
        return new GetAvatarResponse(avatarAppearance, member.getAvatarColor(), member.getNickname());
    }

    /**
     * 아바타 수정
     */
    public PatchAvatarResponse modifyAvatar(Long memberId, PatchAvatarRequest patchAvatarRequest){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        String nickname = patchAvatarRequest.getNickname();
        String avatarColor = patchAvatarRequest.getAvatarColor();

        member.setNickname(nickname);
        member.setAvatarColor(avatarColor);

        memberRepository.save(member);

        return new PatchAvatarResponse(memberId, nickname, avatarColor);
    }

    /**
     * 아바타 외형 수정
     */
    public PatchAvatarAppearanceResponse modifyAvatarAppearance(Long memberId, PatchAvatarAppearanceRequest patchAvatarAppearanceRequest) {
        log.info("[MemberService.modifyAvatarAppearance]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        int skeletalMuscleMass = patchAvatarAppearanceRequest.getSkeletalMuscleMass();
        int bodyFatMass = patchAvatarAppearanceRequest.getFatMass();

        member.setSkeletalMuscleMass(skeletalMuscleMass);
        member.setBodyFatMass(bodyFatMass);

        memberRepository.save(member);

        return new PatchAvatarAppearanceResponse(avatarService.calculateAvatarAppearance(member));
    }

    /**
     * 사용자 목표 조회
     */
    @Transactional(readOnly = true)
    public GetPlanResponse getMemberPlan(Long memberId){
        log.info("[MemberService.getMemberPlan]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        double initialWeight = member.getInitialWeight();
        double targetWeight = member.getTargetWeight();
        String dietType = member.getDietType();
        int recommendedKcal = calRecommendedKcal(member.getSex(), member.getAge(), member.getHeight(), initialWeight, targetWeight);
        int carbohydrateRate = member.getCarbohydrateRate();
        int proteinRate = member.getProteinRate();
        int fatRate = member.getFatRate();
        int targetKcal = member.getTargetKcal();

        return new GetPlanResponse(initialWeight, targetWeight, dietType, recommendedKcal, carbohydrateRate, proteinRate, fatRate, targetKcal);
    }

    /**
     * 사용자 목표 수정(체중, 탄단지 비율, 칼로리)
     */
    public PatchPlanResponse modifyMemberPlan(Long memberId, PatchPlanRequest patchPlanRequest){
        log.info("[MemberService.modifyMemberPlan]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        double initialWeight = patchPlanRequest.getInitialWeight();
        double targetWeight = patchPlanRequest.getTargetWeight();
        String dietType = patchPlanRequest.getDietType();
        // 입력받은 초기체중, 목표체중으로 다시 추천 칼로리 계산
        int recommendedKcal = calRecommendedKcal(member.getSex(), member.getAge(), member.getHeight(), initialWeight, targetWeight);
        int carbohydrateRate = patchPlanRequest.getCarbohydrateRate();
        int proteinRate = patchPlanRequest.getProteinRate();
        int fatRate = patchPlanRequest.getFatRate();
        int targetKcal = patchPlanRequest.getTargetKcal();

        checkNutrientRatio(carbohydrateRate, proteinRate, fatRate);

        member.setInitialWeight(initialWeight);
        member.setTargetWeight(targetWeight);
        member.setDietType(dietType);
        member.setCarbohydrateRate(carbohydrateRate);
        member.setProteinRate(proteinRate);
        member.setFatRate(fatRate);
        member.setTargetKcal(targetKcal);
        // 목표 수정되면 데이터베이스에서 target_updated_at 도 갱신
        member.setTargetUpdatedAt(LocalDate.now());

        memberRepository.save(member);
        return new PatchPlanResponse(initialWeight, targetWeight, recommendedKcal, dietType ,carbohydrateRate,proteinRate,fatRate,targetKcal);
    }

    public void checkNutrientRatio(int carbohydrateRate, int proteinRate, int fatRate){
        int total = carbohydrateRate + proteinRate + fatRate;
        if(total != 100){
            throw new MemberException(RATIO_NOT_CORRECT);
        }
    }

    /**
     * 사용자 목표 조회 (식단타입에 따른 탄단지 조회)
     */
    public GetDietTypeResponse getDietType(Long memberId, String dietType){
        log.info("[MemberService.getDietType]");

        checkMemberExist(memberId); // 존재하는 유저인지 확인

        int[] ratio = calculateRate(dietType);
        int carbohydrateRate = ratio[0];
        int proteinRate = ratio[1];
        int fatRate = ratio[2];

        return new GetDietTypeResponse(dietType, carbohydrateRate, proteinRate,fatRate);
    }

    /**
     * 사용자 목표 조회 (권장 칼로리 반환)
     */
    public GetRecommendedKcalResponse getRecommendedKcal(Long memberId, GetRecommendedKcalRequest getRecommendedKcalRequest){
        log.info("[MemberService.getRecommendedKcal]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        MemberSex sex = member.getSex();
        int age = member.getAge();
        int height = member.getHeight();
        double initialWeight = getRecommendedKcalRequest.getInitialWeight();
        double targetWeight = getRecommendedKcalRequest.getTargetWeight();

        int recommendedKcal = calRecommendedKcal(sex, age, height, initialWeight, targetWeight);

        return new GetRecommendedKcalResponse(recommendedKcal);
    }

    /**
     * 홈화면 현재 날짜, 목표 경과일, 남은 칼로리 조회, 아바타, 목표 칼로리 및 잔여 칼로리, 탄단지 기타 영양소 조회
     */
    public GetProfileResponse getMemberProfile(Long memberId, LocalDate mealDate){
        log.info("[MemberService.getMemberProfile]");
        // 유저 정보 불러오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        // mealDate 에 해당하는 끼니 리스트 모아오기
        Optional<List<Meal>> mealsOptional  = mealRepository.findByMember_MemberIdAndMealDateAndStatus(memberId, mealDate, BaseStatus.A);

        LocalDate date = mealDate; // 입력받은 mealDate 반환
        int elapsedDays = calculateElapsedDays(mealDate, member.getTargetUpdatedAt()); // 몇일차인지 target_updated_at 이랑 mealDate 랑 비교해서 계산
        String nickname = member.getNickname(); // 닉네임
        int remainingKcal = member.getTargetKcal() - calculateIntakeKcal(mealsOptional.get());// 해당 날짜의 남은 칼로리 계산
        String avatarColor = member.getAvatarColor(); // 아바타 색상
        String avatarAppearance = avatarService.calculateAvatarAppearance(member);// 아바타 외형

        // 타겟 칼로리 반환
        int targetKcal = member.getTargetKcal();
        // 원래 섭취해야하는 탄,단,지
        int[] targetRatio = calculateNutrientGram(member);
        int targetCarbohydrate = targetRatio[0];
        int targetProtein = targetRatio[1];
        int targetFat = targetRatio[2];

        GetProfileResponse.Goal goal = new GetProfileResponse.Goal(targetKcal,targetCarbohydrate, targetProtein, targetFat);

        // 섭취한 총 칼로리 반환
        int kcal = calculateIntakeKcal(mealsOptional.get());
        // 섭취한 탄,단,지
        int[] intakeGram = calculateNutrient(mealsOptional.get());
        int carbohydrate = intakeGram[0];
        int protein = intakeGram[1];
        int fat = intakeGram[2];
        // 나트륨, 당, 포화지방, 트랜스지방, 콜레스테롤 반환
        int sodium = intakeGram[3];
        int sugar = intakeGram[4];
        int saturatedFat = intakeGram[5];
        int transFat = intakeGram[6];
        int cholesterol = intakeGram[7];

        GetProfileResponse.Intake intake = new GetProfileResponse.Intake(kcal,carbohydrate,protein,fat,sodium,sugar,saturatedFat,transFat,cholesterol);

        return new GetProfileResponse(date, elapsedDays, remainingKcal, nickname, avatarColor, avatarAppearance, goal, intake);
    }

    /**
     * 섭취한 영양소 계산
     */
    public int[] calculateNutrient(List<Meal> meals){
        int[] intakeGram = new int[8];
        for(Meal meal : meals){
            List<FoodMealMappingTable> foodItems = meal.getFoodMealMappingTables();
            // 각 FoodMealMappingTable 객체에서 음식 정보와 수량 추출
            for (FoodMealMappingTable foodItem : foodItems) {
                // 음식 항목의 ID
                Long foodId = foodItem.getFood().getFoodId();
                // 음식의 수량
                int quantity = foodItem.getQuantity();
                Optional<Food> food = foodRepository.findByFoodIdAndStatus(foodId, BaseStatus.A);

                intakeGram[0] += (int)(food.get().getCarbohydrate() * quantity) / 100; // 탄수화물
                intakeGram[1] += (int)(food.get().getProtein() * quantity) / 100; // 단백질
                intakeGram[2] += (int)(food.get().getFat() * quantity) / 100; // 지방
                intakeGram[3] += (int)(food.get().getSodium() * quantity) / 100; // 나트륨
                intakeGram[4] += (int)(food.get().getSugar() * quantity) / 100; // 당류
                intakeGram[5] += (int)(food.get().getSaturatedFattyAcid() * quantity) / 100; // 포화지방
                intakeGram[6] += (int)(food.get().getTransFatAcid() * quantity) / 100; // 트랜스지방
                intakeGram[7] += (int)(food.get().getCholesterol() * quantity) / 100; // 콜레스테롤
            }
        }
        return intakeGram;
    }

    /**
     * 경과 일수 계산
     */
    public int calculateElapsedDays(LocalDate targetUpdatedAt,LocalDate mealDate){
        Period period = Period.between(mealDate,targetUpdatedAt);
        return period.getDays();
    }

    /**
     * 섭취한 총 칼로리 계산
     */
    public int calculateIntakeKcal(List<Meal> meals){
        int totalIntakeKcal = 0;
        for(Meal meal : meals){
            totalIntakeKcal += (int)foodMealMappingTableService.getMealKcal(meal.getMealId());
        }
        return totalIntakeKcal;
    }

    /**
     * 탄단지 그램수 계산
     */
    public int[] calculateNutrientGram(Member member){
        int[] gram = new int[3];
        int targetKcal = member.getTargetKcal();
        int carbohydrateRate = member.getCarbohydrateRate();
        int proteinRate = member.getProteinRate();
        int fatRate = member.getFatRate();

        gram[0] = targetKcal * carbohydrateRate / 100 / 4;      // 탄수화물
        gram[1] = targetKcal * proteinRate / 100 / 4;           // 단백질
        gram[2] = targetKcal * fatRate / 100 / 9;               // 지방

        return gram;
    }

    /**
     * 유저의 남은 칼로리 & 유저에게 가장 부족한 영양소 계산 (for 치팅데이)
     */
    public Map<String, Object> calculateRemainingKcalAndLackingNutrientName(Long memberId) {
        log.info("[MemberService.calculateRemainingKcalAndLackingNutrientName]");

        // 유저 정보 불러오기
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        // 오늘의 끼니 리스트 모아오기
        Optional<List<Meal>> mealsOptional  = mealRepository.findByMember_MemberIdAndMealDateAndStatus(memberId, LocalDate.now(), BaseStatus.A);

        //--------------------------------------------------

        // [유저의 남은 칼로리 계산]
        int remainingKcal = member.getTargetKcal() - calculateIntakeKcal(mealsOptional.get());// 해당 날짜의 남은 칼로리 계산
        log.info("남은 칼로리 = {}", remainingKcal);

        //--------------------------------------------------

        // [유저에게 가장 부족한 영양소 계산]
        // 원래 섭취해야하는 탄,단,지
        int[] targetRatio = calculateNutrientGram(member);
        int targetCarbohydrate = targetRatio[0];
        int targetProtein = targetRatio[1];
        int targetFat = targetRatio[2];

        // 섭취한 탄,단,지
        int[] intakeGram = calculateNutrient(mealsOptional.get());
        int carbohydrate = intakeGram[0];
        int protein = intakeGram[1];
        int fat = intakeGram[2];

        // 부족한 영양소 계산
        int lackingCarbohydrate = targetCarbohydrate - carbohydrate;
        int lackingProtein = targetProtein - protein;
        int lackingFat = targetFat - fat;

        // 영양소가 부족한 순대로(내림차순) 정렬
        Map<String, Integer> lackingNutrients = new HashMap<>();
        lackingNutrients.put("탄수화물", lackingCarbohydrate);
        lackingNutrients.put("단백질", lackingProtein);
        lackingNutrients.put("지방", lackingFat);

        Map<String, Integer> sortedLackingNutrients = lackingNutrients.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, HashMap::new));

        //--------------------------------------------------
        // 맵에 값을 담아 반환
        Map<String, Object> result = new HashMap<>();

        int i = 1;
        for (Map.Entry<String, Integer> entry : sortedLackingNutrients.entrySet()) {
            log.info("{} 순위 부족 영양소 = {}", i, entry.getKey());
            result.put("lackingNutrient" + i++, entry.getKey());
        }

        result.put("remainingKcal", remainingKcal);
        return result;
    }

    /**
     * 유저의 남은 칼로리 계산 (for 얼마나 먹을까요)
     */
    public int calculateRemainingKcal(Long memberId) {
        // 유저 정보 불러오기
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        // 오늘의 끼니 리스트 모아오기
        Optional<List<Meal>> mealsOptional = mealRepository.findByMember_MemberIdAndMealDateAndStatus(memberId, LocalDate.now(), BaseStatus.A);

        // [유저의 남은 칼로리 계산]
        int remainingKcal = member.getTargetKcal() - calculateIntakeKcal(mealsOptional.get());// 해당 날짜의 남은 칼로리 계산
        remainingKcal = Math.max(0, remainingKcal);

        log.info("남은 칼로리 = {}", remainingKcal);
        return remainingKcal;
    }

    /** 해당 id를 가진 member가 존재하는지 여부 파악, 없으면 MEMBER_NOT_FOUND 에러*/
    public void checkMemberExist(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }
}