package mealplanb.server.dto.meal;

import java.util.HashMap;
import java.util.Map;

public class MealTypeConverter {
    /** ex) 1 -> 첫끼로 바꿔주는 컨버터
     * MealTypeConverter.convertMealTypeLabel(meal.getMealType())
     * 위와 같이 호출해서 사용 가능 */

    private static final Map<Integer, String> MEAL_TYPE_MAP = new HashMap<>();

    static {
        MEAL_TYPE_MAP.put(1, "첫 끼");
        MEAL_TYPE_MAP.put(2, "두 끼");
        MEAL_TYPE_MAP.put(3, "세 끼");
        MEAL_TYPE_MAP.put(4, "네 끼");
        MEAL_TYPE_MAP.put(5, "다섯 끼");
        MEAL_TYPE_MAP.put(6, "여섯 끼");
        MEAL_TYPE_MAP.put(7, "일곱 끼");
        MEAL_TYPE_MAP.put(8, "여덟 끼");
        MEAL_TYPE_MAP.put(9, "아홉 끼");
        MEAL_TYPE_MAP.put(10, "열 끼");
    }

    public static String convertMealTypeLabel(int mealType) {
        return MEAL_TYPE_MAP.getOrDefault(mealType, "값 없음");
    }
}
