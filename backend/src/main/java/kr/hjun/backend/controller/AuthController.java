package kr.hjun.backend.controller;

import jakarta.validation.Valid;
import kr.hjun.backend.dto.*;
import kr.hjun.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    // 회원 가입 요청을 처리한다.
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody UserCreateRequest request) {
        RegistrationResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    // 로그인 요청을 처리한다.
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // 토큰 재발급 요청을 처리한다.
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        TokenResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    // 로그아웃 요청을 처리한다.
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody TokenInvalidateRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }
}
