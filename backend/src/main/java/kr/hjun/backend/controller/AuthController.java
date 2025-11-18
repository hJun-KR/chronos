package kr.hjun.backend.controller;

import jakarta.validation.Valid;
import kr.hjun.backend.dto.LoginRequest;
import kr.hjun.backend.dto.UserCreateRequest;
import kr.hjun.backend.dto.UserResponse;
import kr.hjun.backend.service.UserService;
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

    private final UserService userService;

    // 회원 가입 요청을 처리한다.
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    // 로그인 요청을 처리한다.
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        UserResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
