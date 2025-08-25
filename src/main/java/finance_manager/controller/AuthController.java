package finance_manager.controller;

import finance_manager.dto.LoginResponseDto;
import finance_manager.dto.SignUpResponseDto;
import finance_manager.dto.UserLoginRequest;
import finance_manager.dto.UserSignUpRequest;
import finance_manager.service.AuthService;
import finance_manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<SignUpResponseDto> signup(@Valid @RequestBody UserSignUpRequest signUpRequestDto) {
        return ResponseEntity.ok(authService.signUp(signUpRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/check")
    public ResponseEntity<String> check(HttpSession Session) {
        return ResponseEntity.ok("Auth is Up and Running");
    }
}
