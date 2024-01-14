package mealplanb.server.dto.food;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetFoodAutoCompleteResponse {
    /** 자동완성 검색 */
    private List<String> suggestions;
}
