package finance_manager.repository;

import finance_manager.entity.Category;
import finance_manager.entity.Transaction;
import finance_manager.entity.TransactionType;
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
public class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CategoryRepository categoryRepository;
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
    void shouldfindByUserIdOrderByDateDesc(){
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
}
