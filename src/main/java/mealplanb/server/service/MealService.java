package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MealException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Meal.Meal;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.meal.GetMealFoodResponse.FoodInfo;
import mealplanb.server.dto.meal.GetMealResponse;
import mealplanb.server.dto.meal.*;
import mealplanb.server.dto.meal.GetMealResponse.GetMealItem;
import mealplanb.server.dto.meal.MealTypeConverter;
import mealplanb.server.dto.meal.PostMealFoodRequest.FoodItem;
import mealplanb.server.dto.meal.PostMealRequest;
import mealplanb.server.dto.meal.PostMealResponse;
import mealplanb.server.dto.meal.PatchMealResponse;
import mealplanb.server.repository.MealRepository;
import mealplanb.server.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MealService {
    private final MealRepository mealRepository;
    private final MemberRepository memberRepository;
    private final FoodMealMappingTableService foodMealMappingTableService;

    /**
     * 끼니 등록
     */
    public PostMealResponse postMeal(Long memberId, PostMealRequest mealRequest) {
        log.info("[MealService.postMeal]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        // 이미 존재하는 Meal에 대한 요청이 들어왔을 때 예외처리
        checkDuplicate(mealRequest, member);

        //Meal 테이블에 저장
        Meal meal = new Meal(member, mealRequest.getMealDate(), mealRequest.getMealType());
        Meal createdMeal = mealRepository.save(meal);
        return new PostMealResponse(createdMeal.getMealId());
    }

    private void checkDuplicate(PostMealRequest mealRequest, Member member) {
        boolean exists = mealRepository.existsByMemberAndMealDateAndMealTypeAndStatus(member, mealRequest.getMealDate(), mealRequest.getMealType(), BaseStatus.A);
        if (exists){
            throw new MealException(BaseExceptionResponseStatus.DUPLICATE_MEAL);
        }
    }

    /**
     *  끼니 기록 조회(홈화면)
     */
    public GetMealResponse getMealList(Long memberId, LocalDate mealDate) {
        log.info("[MealService.getMealList]");
        Optional<List<Meal>> mealsOptional  = mealRepository.findByMember_MemberIdAndMealDateAndStatus(memberId, mealDate, BaseStatus.A);

        if (mealsOptional.isEmpty()) { // 결과가 없는 경우
            log.info("[MealService.getMealList] - 만들어진 끼니 없음");
            return new GetMealResponse(mealDate);
        }

        List<GetMealItem> mealItems = makeGetMealItems(mealsOptional.get());
        return new GetMealResponse(mealDate, mealItems);
    }

    private List<GetMealItem> makeGetMealItems(List<Meal> meals) {
        log.info("[MealService.makeGetMealItems]");
        List<GetMealItem> mealItems = new ArrayList<>();
        for (Meal meal : meals){
            GetMealItem getMealItem = new GetMealItem(meal.getMealId() ,MealTypeConverter.convertMealTypeLabel(meal.getMealType()), (int) foodMealMappingTableService.getMealKcal(meal.getMealId()));
            mealItems.add(getMealItem);
        }
        return mealItems;
    }

    /**
     *  끼니 삭제
     */
    @Transactional
    public PatchMealResponse deleteMeal(long mealId, Long memberId) {
        log.info("[MealService.deleteMeal]");
        //해당 mealId, memberId를 가지는 Meal 데이터가 있는지 확인
        Meal meal = mealRepository.findByMealIdAndMember_MemberIdAndStatus(mealId, memberId, BaseStatus.A)
                .orElseThrow(()-> new MealException(BaseExceptionResponseStatus.MEAL_NOT_FOUND));

        meal.setStatus(BaseStatus.D); // Meal의 상태를 업데이트하여 삭제 상태로 변경
        foodMealMappingTableService.deleteFoodMealMapping(mealId); // 해당 mealId를 갖는 FoodMealMappingTable의 상태를 업데이트하여 삭제 상태로 변경
        reduceLaterMealsMealType(meal); //삭제한 끼니 뒷 끼니들의 mealType =- 1

        return new PatchMealResponse(meal.getMealId(), meal.getStatus());
    }

    /**
     *  삭제한 끼니 뒷 끼니들의 mealType =- 1
     */
    private void reduceLaterMealsMealType(Meal deletedMeal) {
        Optional<List<Meal>> laterMealsOptional = mealRepository.findAllByMealDateAndMemberAndMealTypeGreaterThan(
                deletedMeal.getMealDate(),
                deletedMeal.getMember(),
                deletedMeal.getMealType()
        );
        laterMealsOptional.ifPresent(laterMeals -> laterMeals.forEach(Meal::reduceMealType));
    }

    /**
     *  끼니의 식사리스트 등록
     */
    public void postMealFood(Long memberId, PostMealFoodRequest postMealFoodRequest) {
        log.info("[MealService.postMealFood]");

        //해당 mealId를 갖는 status가 A인 Meal이 존재하는 지
        Meal meal = mealRepository.findByMealIdAndStatus(postMealFoodRequest.getMealId(), BaseStatus.A)
                .orElseThrow(()-> new MealException(BaseExceptionResponseStatus.MEAL_NOT_FOUND));

        //해당 Meal의 memberId가 요청을 보낸 멤버의 아이디와 동일한지
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));
        if (!meal.getMember().equals(member)) {
            throw new MealException(BaseExceptionResponseStatus.MEAL_UNAUTHORIZED_ACCESS);
        }

        foodMealMappingTableService.postMealFood(member, meal, postMealFoodRequest.getFoods(), false);
    }

    /**
     * 끼니의 식사 리스트 조회
     */
    public GetMealFoodResponse getMealFoodList(Long memberId, long mealId) {
        log.info("[MealService.getMealFoodList]");
        // 삭제 처리된 끼니일 경우,
        // FoodMealMappingTable에서 주어진 memberId, mealId에 해당하는 status가 A인 음식을 다 찾아보고 없다고 빈 값을 반환하는 것보다
        // Meal에서 삭제된 끼니면 아예 FoodMealMappingTable을 탐색하지 않도록 했다.
        log.info("[MealService.getMealFoodList]");
        Meal meal = mealRepository.findByMealIdAndMember_MemberIdAndStatus(mealId, memberId, BaseStatus.A)
                .orElseThrow(()->new MealException(BaseExceptionResponseStatus.MEAL_NOT_FOUND));

        List<FoodInfo> foodList = foodMealMappingTableService.getMealFoodList(mealId);
        return new GetMealFoodResponse(meal.getMealId(), meal.getMealDate(), meal.getMealType(), foodList);
    }

    /**
     * 채팅을 통한 끼니 등록
     */
    @Transactional
    public void postMealSuggestedFood(Long memberId, FoodItem food) {
        log.info("[MealService.postMealSuggestedFood]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        Optional<Meal> todayLatestMeal = mealRepository.findTodayLatestMeal(memberId, LocalDate.now());

        Meal meal = null;
        List<FoodItem> foodList = new ArrayList<>();
        if (todayLatestMeal.isPresent()){
            log.info("[MealService.postMealSuggestedFood] - 마지막 끼니 존재");
            // 마지막 끼니가 빈끼니면 해당 끼니에 식품 추가
            if(foodMealMappingTableService.isMealEmpty(todayLatestMeal.get().getMealId())){
                log.info("[MealService.postMealSuggestedFood] - 마지막 끼니{} 가 빈끼니", todayLatestMeal.get().getMealId());
                meal = todayLatestMeal.get();
            }/*else if (todayLatestMeal.get().getMealType()==10){
                log.info("[MealService.postMealSuggestedFood] - 마지막 끼니{} 가 빈끼니는 아니지만, 열끼 이상 만들지 않기 위해 해당 끼니에 추가", todayLatestMeal.get().getMealId());
                meal = todayLatestMeal.get();
                List<FoodInfo> mealFoodList = foodMealMappingTableService.getMealFoodList(meal.getMealId());
                mealFoodList.forEach(f -> foodList.add(new FoodItem(f.getFoodId(), f.getQuantity())));
            }*/
        }

        if (meal==null){ // 그렇지 않다면 새로운 끼니를 생성 후, 식품 추가
            log.info("[MealService.postMealSuggestedFood] - 새로운 끼니 생성");
            int mealType = todayLatestMeal.isPresent() ? todayLatestMeal.get().getMealType() + 1 : 1;
            Meal createdMeal = mealRepository.save(new Meal(member, LocalDate.now(), mealType));
            meal = createdMeal;
        }

        log.info("[MealService.postMealSuggestedFood] mealId = {}", meal.getMealId());

        // 끼니에 식품 등록
        foodList.add(food);
        foodMealMappingTableService.postMealFood(member, meal, foodList, true);
    }
}

