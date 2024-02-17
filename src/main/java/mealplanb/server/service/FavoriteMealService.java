package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MealException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.domain.FavoriteMeal;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.meal.GetMyMealListResponse;
import mealplanb.server.dto.meal.GetMyMealResponse;
import mealplanb.server.dto.meal.GetMyMealResponse.FavoriteMealItem;
import mealplanb.server.dto.meal.PostMyMealRequest;
import mealplanb.server.repository.FavoriteMealRepository;
import mealplanb.server.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteMealService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final FavoriteMealRepository favoriteMealRepository;
    private final FavoriteMealComponentService favoriteMealComponentService;

    /**
     * 나의 식단 등록
     */
    @Transactional
    public void postMyMeal(Long memberId, PostMyMealRequest postMyMealRequest){
        log.info("[FavoriteMealService.postMyMeal]");

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));

        String favoriteMealName = postMyMealRequest.getFavoriteMealName();
        checkFavoriteMealNameExist(favoriteMealName); // 동일한 식단 이름 있는지 검사

        FavoriteMeal favoriteMeal = FavoriteMeal.builder()
                .member(member)
                .favoriteMealName(favoriteMealName)
                .status(BaseStatus.A)
                .build();

        favoriteMealRepository.save(favoriteMeal);

        favoriteMealComponentService.saveItems(postMyMealRequest,favoriteMeal);
    }

    private void checkFavoriteMealNameExist(String favoriteMealName) {
        if(favoriteMealRepository.existsByFavoriteMealNameAndStatus(favoriteMealName, BaseStatus.A)){
            throw new MealException(FAVORITE_MEAL_NAME_ALREADY_EXIST);
        }
    }

    /**
     * 나의 식단 조회
     */
    public GetMyMealResponse getMyMeal(Long memberId){
        log.info("[FavoriteMealService.getMyMeal]");

        memberService.checkMemberExist(memberId);

        List<FavoriteMeal> favoriteMeal = favoriteMealRepository.findByMember_MemberIdAndStatus(memberId,BaseStatus.A)
                .orElseThrow(()-> new MealException(FAVORITE_MEAL_NOT_EXIST));

        List<FavoriteMealItem> favoriteMealComponentList = favoriteMealComponentService.getFavoriteMealComponentList(favoriteMeal);
        return new GetMyMealResponse(favoriteMealComponentList);
    }

    /**
     * 나의 식단 삭제
     */
    @Transactional
    public void deleteMyMeal(Long memberId, Long favoriteMealId){
        log.info("[FavoriteMealService.deleteMyMeal]");

        FavoriteMeal favoriteMeal = favoriteMealRepository.findByMember_MemberIdAndFavoriteMealIdAndStatus(memberId, favoriteMealId, BaseStatus.A)
                .orElseThrow(()->new MealException(FAVORITE_MEAL_NOT_EXIST));

        favoriteMeal.updateStatus(BaseStatus.D);

        favoriteMealComponentService.deleteMyMealComponent(favoriteMealId);
    }

    /**
     * 나의 식단 선택해서 식사 리스트 조회하기
     */
    public List<GetMyMealListResponse> getMyMealList(Long memberId, Long favoriteMealId){
        log.info("[FavoriteMealService.getMyMealList]");

        memberService.checkMemberExist(memberId);

        if(!favoriteMealRepository.existsByFavoriteMealIdAndStatus(favoriteMealId, BaseStatus.A)){
            throw new MealException(MEAL_NOT_FOUND); // 식단을 찾을 수 없습니다.
        }
        return favoriteMealComponentService.getMyMealList(favoriteMealId);
    }
}