package finance_manager.service;

import finance_manager.dto.UserSignUpRequest;
import finance_manager.entity.User;
import finance_manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully(){
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");
        request.setPhoneNumber("+911234567890");

        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername(request.getUsername());
        savedUser.setPassword("encodedPassword");
        savedUser.setFullName(request.getFullName());
        savedUser.setPhoneNumber(request.getPhoneNumber());
        userRepository.save(savedUser);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.register(request);

        assertEquals(savedUser.getUsername(), result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
    }
}