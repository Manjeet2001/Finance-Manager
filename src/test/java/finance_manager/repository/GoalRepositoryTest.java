package finance_manager.repository;

import finance_manager.entity.Goal;
import finance_manager.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class GoalRepositoryTest {
    @Autowired
    private GoalRepository goalRepository;
    @Autowired
    private UserRepository userRepository;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("john@example.com");
        user.setPassword("securePass");
        user.setFullName("John Doe");
        user.setPhoneNumber("+1234567890");
        user = userRepository.save(user);
    }

    @Test
    void shouldfindByUserId(){
        Goal goal = new Goal();
        goal.setGoalName("Emergency Fund");
        goal.setTargetAmount(10000.0);
        goal.setStartDate(LocalDate.of(2024, 1, 1));
        goal.setTargetDate(LocalDate.of(2025, 1, 1));
        goal.setUser(user);
        goalRepository.save(goal);

        List<Goal> goals = goalRepository.findByUserId(user.getId());
        assertThat(goals).isNotNull();
        assertThat(goals.get(0).getGoalName()).isEqualTo("Emergency Fund");
    }
}
