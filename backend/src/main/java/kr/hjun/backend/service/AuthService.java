package kr.hjun.backend.service;

import kr.hjun.backend.dto.*;

public interface AuthService {

    // 회원 가입을 처리한다.
    RegistrationResponse register(UserCreateRequest request);

    // 로그인을 처리한다.
    AuthResponse login(LoginRequest request);

    // 리프레시 토큰으로 액세스 토큰을 재발급한다.
    TokenResponse refresh(TokenRefreshRequest request);

    // 리프레시 토큰을 폐기한다.
    void logout(TokenInvalidateRequest request);
}
