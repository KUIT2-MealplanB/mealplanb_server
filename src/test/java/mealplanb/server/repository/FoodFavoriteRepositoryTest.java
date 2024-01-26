package mealplanb.server.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FoodFavoriteRepositoryTest {
    @Autowired
    private FoodFavoriteRepository foodFavoriteRepository;
}