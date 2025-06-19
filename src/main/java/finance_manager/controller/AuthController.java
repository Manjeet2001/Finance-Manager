package finance_manager.controller;

import finance_manager.dto.UserLoginRequest;
import finance_manager.dto.UserSignUpRequest;
import finance_manager.entity.User;
import finance_manager.service.AuthService;
import finance_manager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
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
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginRequest request, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(true);
        String result = authService.login(request, session);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest httpRequest) {
        try {
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                String result = authService.logout(session);
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body("No Active Session");
            }
        } catch (Exception e) {
            System.err.println("Logout controller error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Logout failed: " + e.getMessage());
        }
    }

    @GetMapping("/check")
    public ResponseEntity<String> check(HttpSession Session) {
        return ResponseEntity.ok("Auth is Up and Running");
    }
}
