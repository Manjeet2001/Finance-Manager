package finance_manager;

import finance_manager.entity.*;
import finance_manager.repository.CategoryRepository;
import finance_manager.repository.GoalRepository;
import finance_manager.repository.TransactionRepository;
import finance_manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class RepositoryTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private GoalRepository goalRepository;

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
    void testFindByUsername() {
        Optional<User> found = userRepository.findByUsername("john@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("John Doe");
    }

    @Test
    void testCategoryRepositoryCustomQueries() {
        Category cat = new Category();
        cat.setName("Food");
        cat.setCustom(true);
        cat.setType(TransactionType.EXPENSE);
        cat.setUser(user);
        categoryRepository.save(cat);

        // findByNameAndUserIdOrIsCustom
        Optional<Category> found = categoryRepository.findByNameAndUserIdOrIsCustom("Food", user.getId());
        assertThat(found).isPresent();

        // findByUserIdOrIsCustomFalse
        List<Category> userCats = categoryRepository.findByUserIdOrIsCustomFalse(user.getId());
        assertThat(userCats).isNotNull();

        // existsByNameAndUserId
        boolean exists = categoryRepository.existsByNameAndUserId("Food", user.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void testTransactionRepository() {
        Transaction tx = new Transaction();
        Category cat = new Category();
        cat.setName("Salary");
        cat.setType(TransactionType.INCOME);
        categoryRepository.save(cat);
        tx.setAmount(1000.0);
        tx.setCategory(cat);
        tx.setDate(LocalDate.of(2024, 1, 15));
        tx.setType(TransactionType.INCOME);
        tx.setUser(user);
        transactionRepository.save(tx);

        List<Transaction> txs = transactionRepository.findByUserIdOrderByDateDesc(user.getId());
        assertThat(txs).isNotNull();
        assertThat(txs.get(0).getAmount()).isEqualTo(1000.0);
    }

    @Test
    void testGoalRepository() {
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
