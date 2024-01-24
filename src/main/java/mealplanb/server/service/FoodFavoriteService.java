package mealplanb.server.service;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.domain.Base.BaseStatus;
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
        boolean isFavorite = foodFavoriteRepository.existsByFood_FoodIdAndMember_MemberIdAndStatus(foodId, memberId, BaseStatus.A);
        return isFavorite;
    }
}
