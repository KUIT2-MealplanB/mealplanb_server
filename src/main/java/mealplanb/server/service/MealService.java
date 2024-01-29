package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MealException;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Meal;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.meal.PostMealRequest;
import mealplanb.server.dto.meal.PostMealResponse;
import mealplanb.server.repository.FoodRepository;
import mealplanb.server.repository.MealRepository;
import mealplanb.server.repository.MemberRepository;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class MealService {
    private final MealRepository mealRepository;
    private final MemberRepository memberRepository;

    public PostMealResponse postMeal(Long memberId, PostMealRequest mealRequest) {
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
        boolean exists = mealRepository.existsByMemberAndMealDateAndMealType(member, mealRequest.getMealDate(), mealRequest.getMealType());
        if (exists){
            throw new MealException(BaseExceptionResponseStatus.DUPLICATE_MEAL);
        }
    }

}

