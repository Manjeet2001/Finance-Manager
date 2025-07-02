package finance_manager.service;

import finance_manager.dto.GoalRequest;
import finance_manager.entity.Goal;
import finance_manager.entity.Transaction;
import finance_manager.entity.TransactionType;
import finance_manager.entity.User;
import finance_manager.exception.custom_exception.ForbiddenException;
import finance_manager.exception.custom_exception.ResourceNotFoundException;
import finance_manager.exception.custom_exception.UnauthorizedException;
import finance_manager.repository.GoalRepository;
import finance_manager.repository.TransactionRepository;
import finance_manager.repository.TransactionRepositoryTest;
import finance_manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private HttpSession session;

    private final Long USER_ID = 1L;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(USER_ID);
        user.setUsername("john@example.com");
    }

    @Test
    void shouldCreateGoalSuccessfully() {
        GoalRequest request = new GoalRequest();
        request.setGoalName("Buy Laptop");
        request.setGoalAmount(50000.0);
        request.setTargetDate(LocalDate.now().plusDays(30).format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd")));

        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(goalRepository.save(any(Goal.class))).thenAnswer(inv -> inv.getArgument(0));

        Goal result = goalService.createGoal(request, session);

        assertEquals("Buy Laptop", result.getGoalName());
        assertEquals(50000.0, result.getTargetAmount());
        assertEquals(user, result.getUser());
    }

    @Test
    void shouldThrowIfGoalDateIsPast() {
        GoalRequest request = new GoalRequest();
        request.setGoalName("Old Goal");
        request.setGoalAmount(1000.0);
        request.setTargetDate(LocalDate.now().minusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd")));

        when(session.getAttribute("userId")).thenReturn(USER_ID);

        assertThrows(RuntimeException.class, () -> goalService.createGoal(request, session));
    }

    @Test
    void shouldReturnGoalsWithProgress() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setGoalName("Vacation");
        goal.setTargetAmount(10000.0);
        goal.setTargetDate(LocalDate.now().plusMonths(2));
        goal.setStartDate(LocalDate.now().minusDays(10));
        goal.setUser(user);

        Transaction income = new Transaction();
        income.setDate(LocalDate.now().minusDays(5));
        income.setAmount(5000.0);
        income.setType(TransactionType.INCOME);
        income.setUser(user);

        Transaction expense = new Transaction();
        expense.setDate(LocalDate.now().minusDays(4));
        expense.setAmount(2000.0);
        expense.setType(TransactionType.EXPENSE);
        expense.setUser(user);

        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(goalRepository.findByUserId(USER_ID)).thenReturn(List.of(goal));
        when(transactionRepository.findByUserIdOrderByDateDesc(USER_ID)).thenReturn(List.of(income, expense));

        List<Map<String, Object>> results = goalService.getGoalsWithProgress(session);

        assertEquals(1, results.size());
        Map<String, Object> goalMap = results.get(0);
        assertEquals(3000.0, (Double) goalMap.get("currentProgress"));
        assertEquals(30.0, (Double) goalMap.get("currentPercentage"));
        assertEquals(7000.0, (Double) goalMap.get("remainingProgress"));
    }

    @Test
    void shouldUpdateGoalSuccessfully() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setUser(user);
        goal.setGoalName("Emergency Fund");
        goal.setTargetAmount(1000.0);

        GoalRequest request = new GoalRequest();
        request.setGoalAmount(2000.0);
        request.setTargetDate(LocalDate.now().plusDays(10).toString());

        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(inv -> inv.getArgument(0));

        Goal updated = goalService.updateGoal(1L, request, session);

        assertEquals(2000.0, updated.getTargetAmount());
        assertEquals(LocalDate.parse(request.getTargetDate()), updated.getTargetDate());
    }

    @Test
    void shouldThrowForbiddenExceptionWhenUpdatingAnotherUsersGoal() {
        Goal goal = new Goal();
        goal.setId(1L);

        User otherUser = new User();
        otherUser.setId(999L);
        goal.setUser(otherUser);

        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        assertThrows(ForbiddenException.class, () ->
                goalService.updateGoal(1L, new GoalRequest(), session));
    }

    @Test
    void shouldDeleteGoalSuccessfully() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setUser(user);

        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        goalService.deleteGoal(1L, session);

        verify(goalRepository).delete(goal);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentGoal() {
        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                goalService.deleteGoal(1L, session));
    }

    @Test
    void shouldThrowUnauthorizedIfUserIdMissingFromSession() {
        when(session.getAttribute("userId")).thenReturn(null);

        assertThrows(UnauthorizedException.class, () ->
                goalService.getGoalsWithProgress(session));
    }
}