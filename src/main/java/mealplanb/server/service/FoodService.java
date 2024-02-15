package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Food.Food;
import mealplanb.server.domain.Food.FoodManager;
import mealplanb.server.domain.Food.FoodUnit;
import mealplanb.server.dto.chat.GetAmountSuggestionResponse;
import mealplanb.server.dto.chat.GetCheatDayFoodResponse.cheatDayFoodInfo;
import mealplanb.server.dto.food.*;
import mealplanb.server.dto.food.GetFavoriteFoodResponse.FoodItem;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.food.GetFoodResponse;
import mealplanb.server.dto.food.PostNewFoodRequest;
import mealplanb.server.dto.food.PostNewFoodResponse;
import mealplanb.server.repository.FoodRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import mealplanb.server.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final FavoriteFoodService favoriteFoodService;
    private final MemberRepository memberRepository;

    /**
     * 특정 식사의 상세 정보 반환
     */
    public GetFoodResponse getFoodDetail(long memberId, long foodId) {
        log.info("[FoodService.getFoodDetail]");
        Food food = foodRepository.findByFoodIdAndStatus(foodId, BaseStatus.A)
                .orElseThrow(()-> new FoodException(BaseExceptionResponseStatus.FOOD_NOT_FOUND));
        return new GetFoodResponse(
                food.getFoodId(),
                food.getName(),
                food.getQuantity(),
                food.getKcal(),
                food.getCarbohydrate(),
                food.getProtein(),
                food.getFat(),
                favoriteFoodService.isFavorite(memberId, foodId)
        );
    }

    /**
     * 식사 등록 by 사용자
     */
    public PostNewFoodResponse postNewFood(Long memberId, PostNewFoodRequest postNewFoodRequest) {
        log.info("[FoodService.postNewFood]");
        Food newFood = new Food(memberId, postNewFoodRequest);
        foodRepository.save(newFood);
        return new PostNewFoodResponse(
                newFood.getFoodId(),
                newFood.getName(),
                newFood.getCategory(),
                newFood.getKeyNutrient(),
                newFood.getQuantity(),
                newFood.getKcal(),
                newFood.getCarbohydrate(),
                newFood.getProtein(),
                newFood.getFat(),
                newFood.getSugar(),
                newFood.getSodium(),
                newFood.getCholesterol(),
                newFood.getSaturatedFattyAcid(),
                newFood.getTransFatAcid()
        );
    }

    /**
     * 사용자 등록 식품(=식사) 삭제
     */
    @Transactional
    public void deleteUserCreatedFood(Long memberId, long foodId) {
        log.info("[FoodService.getFoodDetail]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));
        Food food = foodRepository.findById(foodId)
                .orElseThrow(()-> new FoodException(BaseExceptionResponseStatus.FOOD_NOT_FOUND));

        // 사용자가 식품을 지울 권한이 있는지 검증
        if (!memberId.equals(food.getCreateMemberId())){
            new FoodException(BaseExceptionResponseStatus.UNAUTHORIZED_ACCESS);
        }
        food.setStatus(BaseStatus.D);
    }

    /**
     * 자동완성 검색
     */
    public GetFoodAutoCompleteResponse getAutoComplete(String query, int page, int size) {
        log.info("[FoodService.GetFoodAutoCompleteResponse]");
        Pageable pageable = PageRequest.of(page, size);
        Page<Food> autoComplete = foodRepository.getAutoComplete(query.strip(), pageable);
        List<FoodItem> foods = autoComplete.getContent().stream()
                .map(FoodItem::new) // autoComplete.getContent()를 하면 List<Food>가 결과로 나오는데 각 요소인 Food를 FoodItem으로 변환
                .collect(Collectors.toList());
        return new GetFoodAutoCompleteResponse(page, autoComplete.getTotalPages(), foods);
    }

    /**
     * 채팅(치팅데이)
     */
    public List<cheatDayFoodInfo> getCheatDayFood(int remainingKcal, String lackingNutrientName, String category) {
        log.info("[FoodService.getCheatDayFood]");

        Optional<List<Food>> cheatDayFoodOptional= foodRepository.getCheatDayFood(remainingKcal, lackingNutrientName, category);
        List<cheatDayFoodInfo> cheatDayFoodInfoList = new ArrayList<>(); // 반환값

        if (cheatDayFoodOptional.isPresent()) {
            for (Food cheatDayFood : cheatDayFoodOptional.get()){
                FoodUnit foodUnit = getFoodUnit(cheatDayFood);
                addCheatDayFoodInfo(cheatDayFoodInfoList, remainingKcal, cheatDayFood, foodUnit);
            }
        }else{
            //로그용 else 구문
            log.info("[FoodService.getCheatDayFood]- 해당하는 음식이 존재하지 않음");
            //존재하지 안더라도 오류가 아니므로 아래에서 빈 배열을 반환
        }
        return cheatDayFoodInfoList;
    }


    /**cheatDayFoodInfo를 만들어서 cheatDayFoodInfoList에 넣어준다. */
    private void addCheatDayFoodInfo(List<cheatDayFoodInfo> cheatDayFoodInfoList, int remainingKcal, Food cheatDayFood, FoodUnit foodUnit) {

        int unitGram = foodUnit.getUnitGram();
        String unitName = foodUnit.getUnitName();
        int offer = calculateOffer(remainingKcal, cheatDayFood, unitGram, unitName);
        int offerCarbohydrate = (int) (cheatDayFood.getCarbohydrate() * (unitGram /100) * offer);
        int offerProtein = (int) (cheatDayFood.getProtein() * (unitGram /100) * offer);
        int offerFat = (int) (cheatDayFood.getFat() * (unitGram /100) * offer);
        log.info("-----and offerCarbohydrate={}, offerProtein={}, offerFat={}",
                offerCarbohydrate, offerProtein, offerFat);

        if (offer!=0){
            cheatDayFoodInfoList.add( new cheatDayFoodInfo(
                            cheatDayFood.getFoodId(),
                            cheatDayFood.getName(),
                            offerCarbohydrate,
                            offerProtein,
                            offerFat,
                            offer+ unitName));
        }
    }

    /** 제공량 계산 */
    private int calculateOffer(int remainingKcal, Food food, int unitGram, String unitName) {
        double unitKcal = unitGram * (food.getKcal() /100);
        int offer = (int) (remainingKcal / unitKcal);
        log.info("[FoodService.calculateOffer]  foodName: {}, unitKcal ={}, offer ={}, offerKcal= {}, remainingKcal = {}", food.getName(), unitKcal, offer+unitName, unitKcal*offer, remainingKcal);
        return offer;
    }

    /** 식품의 단위 정보 얻기 */
    private FoodUnit getFoodUnit(Food food){
        FoodUnit foodUnit = new FoodUnit(1,"g");
        if (FoodManager.isContainsKey(food.getCategory())) { // 단위정보가 있는 음식의 경우
            foodUnit = FoodManager.getFoodUnit(food.getCategory());
        }else if (food.getCategory().equals("분식")){ //분식 카테고리는 음식이름이 카테고리
            foodUnit = FoodManager.getFoodUnit(food.getCategory());
        }
        return foodUnit;
    }

    /**
     * 얼마나 먹을까요
     */
    public GetAmountSuggestionResponse getAmountSuggestion(int remainingKcal, Long foodId) {
        Food food = foodRepository.findByFoodIdAndStatus(foodId, BaseStatus.A)
                .orElseThrow(()-> new FoodException(BaseExceptionResponseStatus.FOOD_NOT_FOUND));

        FoodUnit foodUnit = getFoodUnit(food);
        int offer = calculateOffer(remainingKcal, food, foodUnit.getUnitGram(), foodUnit.getUnitName());
        int offerKcal = (int) (foodUnit.getUnitGram() * (food.getKcal() /100) * offer);

        return new GetAmountSuggestionResponse(food.getName(), offer+foodUnit.getUnitName(), offerKcal, remainingKcal);
    }
}