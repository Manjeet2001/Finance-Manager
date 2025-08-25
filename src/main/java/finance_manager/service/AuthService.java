package finance_manager.service;

import finance_manager.dto.LoginResponseDto;
import finance_manager.dto.SignUpResponseDto;
import finance_manager.dto.UserLoginRequest;
import finance_manager.dto.UserSignUpRequest;
import finance_manager.entity.User;
import finance_manager.repository.UserRepository;
import finance_manager.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto login(UserLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String token = authUtil.generateToken(user);
        return new LoginResponseDto(token, user.getId());
    }

    public SignUpResponseDto signUp(UserSignUpRequest request) {
        // Check if username is already taken
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        String role = request.getRole();
        if (role == null || role.isBlank()) {
            role = "ROLE_USER"; // Default
        } else if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        }
        // Create and save new user
        User newUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .role(role)
                .build();

        User savedUser = userRepository.save(newUser);
        return new SignUpResponseDto(savedUser.getId(), savedUser.getUsername());
    }
}