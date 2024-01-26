package mealplanb.server.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class FoodRepositoryTest {
    @Autowired
    private FoodRepository foodRepository;

}