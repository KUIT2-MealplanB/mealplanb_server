package mealplanb.server.repository;

import mealplanb.server.domain.FoodFavorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FoodRepositoryTest {
    @Autowired
    private FoodRepository foodRepository;

}