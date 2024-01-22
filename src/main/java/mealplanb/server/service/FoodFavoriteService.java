package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.repository.FoodFavoriteRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodFavoriteService {
    private final FoodFavoriteRepository foodFavoriteRepository;

    /**
     * 즐겨찾기한 음식인지 여부 반환
     */
    public Boolean isFavorite(long memberId, long foodId){
        boolean isFavorite = foodFavoriteRepository.existsByFoodIdAndMemberIdAndStatus(foodId, memberId, "A");
        return isFavorite;
    }
}
