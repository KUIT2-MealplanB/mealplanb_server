package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.domain.Base.BaseStatus;
import mealplanb.server.repository.FavoriteFoodRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteFoodService {
    private final FavoriteFoodRepository favoriteFoodRepository;

    /**
     * 즐겨찾기한 음식인지 여부 반환
     */
    public Boolean isFavorite(long memberId, long foodId){
        System.out.println("[FavoriteFoodService.isFavorite]");
        boolean isFavorite = favoriteFoodRepository.existsByFood_FoodIdAndMember_MemberIdAndStatus(foodId, memberId, BaseStatus.A);
        return isFavorite;
    }
}