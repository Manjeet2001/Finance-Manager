package finance_manager.repository;

import finance_manager.entity.Category;
import finance_manager.entity.TransactionType;
import finance_manager.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;

    @Mock
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("john@example.com");
        user.setPassword("securePass");
        user.setFullName("John Doe");
        user.setPhoneNumber("+1234567890");
        user = userRepository.save(user);
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
}
