package finance_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import finance_manager.dto.UserSignUpRequest;
import finance_manager.entity.User;
import finance_manager.service.AuthService;
import finance_manager.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@WithMockUser
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldRegisterUser() throws Exception {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");
        request.setPhoneNumber("+911234567890");

        User user = new User(1L, request.getUsername(), "encoded", request.getFullName(), request.getPhoneNumber());

        when(userService.register(any())).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("user registered successfully with id: " + user.getId())));
    }
}
