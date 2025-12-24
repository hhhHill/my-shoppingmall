package com.example.mall.auth;

import com.example.mall.auth.dto.AuthResponse;
import com.example.mall.auth.dto.LoginRequest;
import com.example.mall.auth.dto.RegisterRequest;
import com.example.mall.user.User;
import com.example.mall.user.UserRepository;
import com.example.mall.web.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok(ApiResponse.ok("注册成功", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(req)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout() {
        // JWT stateless logout: frontend can just drop the token
        return ResponseEntity.ok(ApiResponse.ok("已登出", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> me(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.ok(ApiResponse.ok(null));
        }
        User u = userRepository.findByUsername(principal.getUsername()).orElse(null);
        return ResponseEntity.ok(ApiResponse.ok(u == null ? null : new SimpleUser(u.getId(), u.getUsername(), u.getRole().name())));
    }

    record SimpleUser(Long id, String username, String role) {}
}
