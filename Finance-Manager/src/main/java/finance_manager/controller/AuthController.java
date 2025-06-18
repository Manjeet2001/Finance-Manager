package finance_manager.controller;

import finance_manager.dto.UserLoginRequest;
import finance_manager.dto.UserSignUpRequest;
import finance_manager.entity.User;
import finance_manager.service.AuthService;
import finance_manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api.auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignUpRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok("user registered successfully with id: " + user.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginRequest request, HttpSession Session) {
        return ResponseEntity.ok(authService.login(request, Session));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession Session) {
        return ResponseEntity.ok(authService.logout(Session));
    }
}
