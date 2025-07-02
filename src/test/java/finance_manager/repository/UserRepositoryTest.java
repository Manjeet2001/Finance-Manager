package finance_manager.repository;

import finance_manager.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByUsername() {
        User user = new User(null, "test@example.com", "pass", "Tester", "Test User", "+911234567890");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("Tester", found.get().getFullName());
    }
}
