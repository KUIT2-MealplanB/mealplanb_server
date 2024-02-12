package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.FoodException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.Food;
import mealplanb.server.dto.chat.GetCheatDayFoodResponse;
import mealplanb.server.dto.chat.GetCheatDayFoodResponse.cheatDayFoodInfo;
import mealplanb.server.dto.food.*;
import mealplanb.server.dto.food.GetFavoriteFoodResponse.FoodItem;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.food.GetFoodResponse;
import mealplanb.server.dto.food.PostNewFoodRequest;
import mealplanb.server.dto.food.PostNewFoodResponse;
import mealplanb.server.dto.meal.GetMealFoodResponse;
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
        //todo: 코드 이렇게 길게 안하는 방법은 없을지...
        //todo: 사실 PostNewFoodResponse랑 Food랑 status, updatedAt, createdAt 유무 차이라서 Food를 반환하는게 나을려나.
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
            Map<String, Map<String, Object>> foodUnitMap = getFoodUnitMap(); // 치팅데이 단위 Map

            if (foodUnitMap.containsKey(category)||category=="분식") { // 단위정보가 있는 음식의 경우
                unitSuggestion(remainingKcal, category, cheatDayFoodOptional, cheatDayFoodInfoList, foodUnitMap);
            }else {
                gramSuggestion(remainingKcal, cheatDayFoodOptional, cheatDayFoodInfoList);
            }
        }
        return cheatDayFoodInfoList;
    }


    /** 치팅데이 단위가 있는 음식 추천 로직 */
    private void unitSuggestion(int remainingKcal, String category, Optional<List<Food>> cheatDayFoodOptional, List<cheatDayFoodInfo> cheatDayFoodInfoList, Map<String, Map<String, Object>> foodUnitMap) {
        Map<String, Object> categoryInfo = foodUnitMap.get(category); // unitGram:?, unitName:?

        for (Food cheatDayFood : cheatDayFoodOptional.get()){

            // 분식의 경우 해당 음식의 이름으로 categoryInfo 를 갱신 (분식의 경우 categoryInfo 가 null 이었을것임)
            if(cheatDayFood.getCategory()=="분식"){
                categoryInfo = foodUnitMap.get(cheatDayFood.getName());
            }
            int unitGram = (int) categoryInfo.get("unitGram");
            String unitName = (String) categoryInfo.get("unitName");

            addCheatDayFoodInfo(remainingKcal, cheatDayFoodInfoList, cheatDayFood, unitGram, unitName);
        }
    }

    /** 치팅데이 단위가 없는 음식 추천 로직 */
    private void gramSuggestion(int remainingKcal, Optional<List<Food>> cheatDayFoodOptional, List<cheatDayFoodInfo> cheatDayFoodInfoList) {
        int unitGram = 100;
        String unitName = "g";
        for (Food cheatDayFood : cheatDayFoodOptional.get()){
            addCheatDayFoodInfo(remainingKcal, cheatDayFoodInfoList, cheatDayFood, unitGram, unitName);
        }
    }

    /** unitGram에 따라 얼마나 먹어야하는 지(=offer)를 정해주고, cheatDayFoodInfo를 만들어서 cheatDayFoodInfoList에 넣어준다. */
    private void addCheatDayFoodInfo(int remainingKcal, List<cheatDayFoodInfo> cheatDayFoodInfoList, Food cheatDayFood, int unitGram, String unitName) {
        Long foodId = cheatDayFood.getFoodId();
        String name = cheatDayFood.getName();
        double unitKcal = cheatDayFood.getKcal() * (unitGram /100);
        int offer = (int) (remainingKcal / unitKcal);
        int offerCarbohydrate = (int) (cheatDayFood.getCarbohydrate() * (unitGram /100) * offer);
        int offerProtein = (int) (cheatDayFood.getProtein() * (unitGram /100) * offer);
        int offerFat = (int) (cheatDayFood.getFat() * (unitGram /100) * offer);
        log.info("CheatDayFoodInfo added: FoodId={}, Name={}, Offer={}{}, offerKcal= {}, (remainingKcal = {}, unitKcal = {}) offerCarbohydrate={}, offerProtein={}, offerFat={}",
                foodId, name, offer, unitName, offer*unitKcal, remainingKcal, unitKcal, offerCarbohydrate, offerProtein, offerFat);

        if (offer!=0){
            cheatDayFoodInfoList.add(new cheatDayFoodInfo(foodId, name, offerCarbohydrate, offerProtein, offerFat, offer+ unitName));
        }
    }

    /** 치팅데이 단위 관리 맵 */
    private Map<String, Map<String, Object>> getFoodUnitMap() {
        Map<String, Map<String, Object>> foodMap = new HashMap<>();
        foodMap.put("치킨",
                Map.of("unitGram", 100, "unitName", "조각"));
        foodMap.put("피자",
                Map.of("unitGram", 100, "unitName", "조각"));
        foodMap.put("면류",
                Map.of("unitGram", 100, "unitName", "개"));
        foodMap.put("햄버거",
                Map.of("unitGram", 250, "unitName", "개"));
        foodMap.put("떡볶이",
                Map.of("unitGram", 200, "unitName", "인분"));
        foodMap.put("김말이",
                Map.of("unitGram", 40, "unitName", "개"));
        foodMap.put("오징어튀김",
                Map.of("unitGram", 40, "unitName", "개"));
        foodMap.put("어묵",
                Map.of("unitGram", 50, "unitName", "개"));
        foodMap.put("순대",
                Map.of("unitGram", 200, "unitName", "인분"));
        return foodMap;
    }
}