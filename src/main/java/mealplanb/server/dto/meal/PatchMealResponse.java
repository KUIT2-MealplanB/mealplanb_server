package mealplanb.server.dto.meal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mealplanb.server.domain.Base.BaseStatus;

@Getter
@AllArgsConstructor
public class PatchMealResponse {
    /** 끼니 삭제 */
    private Long mealId;
    private BaseStatus status;
}
