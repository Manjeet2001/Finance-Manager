package finance_manager.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalEntityTest {
    @Mock
    private Goal goal;
    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        goal = new Goal();

        user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");
    }

    @Test
    void testGoalCreation() {
        String goalName = "Emergency Fund";
        Double targetAmount = 10000.0;
        LocalDate targetDate = LocalDate.of(2027, 1, 1);
        LocalDate startDate = LocalDate.of(2025, 6, 18);

        goal.setGoalName(goalName);
        goal.setTargetAmount(targetAmount);
        goal.setTargetDate(targetDate);
        goal.setStartDate(startDate);
        goal.setUser(user);

        assertNotNull(goal);
        assertEquals(goalName, goal.getGoalName());
        assertEquals(targetAmount, goal.getTargetAmount());
        assertEquals(targetDate, goal.getTargetDate());
        assertEquals(startDate, goal.getStartDate());
        assertEquals(user, goal.getUser());
    }

    @Test
    void testGoalWithAllArgsConstructor() {
        Long id = 1L;
        String goalName = "Vacation Fund";
        Double targetAmount = 5000.0;
        LocalDate targetDate = LocalDate.of(2027, 12, 1);
        LocalDate startDate = LocalDate.of(2025, 6, 18);

        Goal newGoal = new Goal(id, goalName, targetAmount, targetDate, startDate, user);

        assertEquals(id, newGoal.getId());
        assertEquals(goalName, newGoal.getGoalName());
        assertEquals(targetAmount, newGoal.getTargetAmount());
        assertEquals(targetDate, newGoal.getTargetDate());
        assertEquals(startDate, newGoal.getStartDate());
        assertEquals(user, newGoal.getUser());
    }

    @Test
    void testGoalWithNoArgsConstructor() {
        Goal newGoal = new Goal();

        assertNotNull(newGoal);
        assertNull(newGoal.getId());
        assertNull(newGoal.getGoalName());
        assertNull(newGoal.getTargetAmount());
        assertNull(newGoal.getTargetDate());
        assertNull(newGoal.getStartDate());
        assertNull(newGoal.getUser());
    }

    @Test
    void testGoalUserRelationship() {
        goal.setUser(user);

        assertEquals(user, goal.getUser());
        assertEquals(1L, goal.getUser().getId());
    }

    @Test
    void testGoalDateValidation() {
        LocalDate startDate = LocalDate.of(2025, 6, 18);
        LocalDate targetDate = LocalDate.of(2027, 1, 1);

        goal.setStartDate(startDate);
        goal.setTargetDate(targetDate);

        assertTrue(goal.getStartDate().isBefore(goal.getTargetDate()));
    }

    @Test
    void testGoalEqualsAndHashCode() {
        Goal goal1 = new Goal();
        goal1.setId(1L);
        goal1.setGoalName("Emergency Fund");
        goal1.setTargetAmount(10000.0);
        goal1.setTargetDate(LocalDate.of(2027, 1, 1));
        goal1.setStartDate(LocalDate.of(2025, 6, 18));

        Goal goal2 = new Goal();
        goal2.setId(1L);
        goal2.setGoalName("Emergency Fund");
        goal2.setTargetAmount(10000.0);
        goal2.setTargetDate(LocalDate.of(2027, 1, 1));
        goal2.setStartDate(LocalDate.of(2025, 6, 18));

        assertEquals(goal1, goal2);
        assertEquals(goal1.hashCode(), goal2.hashCode());
    }

    @Test
    void testGoalToString() {
        goal.setId(1L);
        goal.setGoalName("Emergency Fund");
        goal.setTargetAmount(10000.0);

        String goalString = goal.toString();

        assertNotNull(goalString);
        assertTrue(goalString.contains("Emergency Fund"));
        assertTrue(goalString.contains("10000.0"));
    }

    @Test
    void testGoalAmountValidation() {
        Double positiveAmount = 5000.0;
        Double zeroAmount = 0.0;
        Double negativeAmount = -1000.0;

        goal.setTargetAmount(positiveAmount);
        assertEquals(positiveAmount, goal.getTargetAmount());
        assertTrue(goal.getTargetAmount() > 0);

        goal.setTargetAmount(zeroAmount);
        assertEquals(zeroAmount, goal.getTargetAmount());

        goal.setTargetAmount(negativeAmount);
        assertEquals(negativeAmount, goal.getTargetAmount());
    }

    @Test
    void testGoalDateRelationships() {
        LocalDate currentDate = LocalDate.now();
        LocalDate pastDate = currentDate.minusDays(30);
        LocalDate futureDate = currentDate.plusDays(365);

        goal.setStartDate(pastDate);
        goal.setTargetDate(futureDate);

        assertTrue(goal.getStartDate().isBefore(currentDate));
        assertTrue(goal.getTargetDate().isAfter(currentDate));
        assertTrue(goal.getStartDate().isBefore(goal.getTargetDate()));
    }
}