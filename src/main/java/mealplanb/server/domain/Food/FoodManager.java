package mealplanb.server.domain.Food;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FoodManager {
    /**식품 단위 관리 맵 */
    private static final Map<String, FoodUnit> foodUnitMap = initializeFoodUnitMap();

    private static Map<String, FoodUnit> initializeFoodUnitMap() {
        return Map.of("치킨", new FoodUnit(100, "조각"),
                "피자", new FoodUnit(100, "조각"),
                "면류", new FoodUnit(100, "개"),
                "버거", new FoodUnit(250, "개"),
                "떡볶이", new FoodUnit(200, "인분"),
                "김말이", new FoodUnit(40, "개"),
                "오징어튀김", new FoodUnit(40, "개"),
                "어묵", new FoodUnit(50, "개"),
                "순대", new FoodUnit(200, "인분"));
    }

    public static FoodUnit getFoodUnit(String foodName) {
        return foodUnitMap.get(foodName);
    }

    public static boolean isContainsKey(String key){
        return foodUnitMap.containsKey(key);
    }
}