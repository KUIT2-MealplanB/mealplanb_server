package mealplanb.server.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class GetDietTypeRequest {
    /**
     * 사용자 목표 조회 (식단타입에 따른 탄단지 조회)
     */
    private String dietType;
}