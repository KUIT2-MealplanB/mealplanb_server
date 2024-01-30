package mealplanb.server.dto.meal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PostMealRequest {
    /** 끼니 생성 */
    private int mealType;
    private LocalDate mealDate;
}
