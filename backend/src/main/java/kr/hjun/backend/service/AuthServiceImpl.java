package kr.hjun.backend.service;

import kr.hjun.backend.config.JwtProperties;
import kr.hjun.backend.dto.*;
import kr.hjun.backend.entity.RefreshToken;
import kr.hjun.backend.entity.User;
import kr.hjun.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationService emailVerificationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    // 회원 가입을 처리한다.
    @Override
    public RegistrationResponse register(UserCreateRequest request) {
        User user = userService.register(request);
        emailVerificationService.sendVerification(user);
        UserResponse userResponse = new UserResponse(user);
        return new RegistrationResponse(userResponse, "이메일 인증 후 이용 가능합니다.");
    }

    // 로그인을 처리한다.
    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userService.login(request);
        TokenResponse tokenResponse = issueTokens(user);
        return new AuthResponse(new UserResponse(user), tokenResponse);
    }

    // 리프레시 토큰으로 액세스 토큰을 재발급한다.
    @Override
    public TokenResponse refresh(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.rotate(request.getRefreshToken());
        String accessToken = jwtTokenProvider.generateAccessToken(refreshToken.getUser());
        return TokenResponse.of(accessToken, refreshToken.getToken(), jwtProperties.getAccessTokenValidity());
    }

    // 리프레시 토큰을 폐기한다.
    @Override
    public void logout(TokenInvalidateRequest request) {
        refreshTokenService.revoke(request.getRefreshToken());
    }

    // 사용자의 비밀번호를 변경한다.
    @Override
    public void changePassword(Long userId, PasswordChangeRequest request) {
        userService.changePassword(userId, request);
    }

    // 사용자에게 액세스/리프레시 토큰을 발급한다.
    private TokenResponse issueTokens(User user) {
        RefreshToken refreshToken = refreshTokenService.create(user);
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        return TokenResponse.of(accessToken, refreshToken.getToken(), jwtProperties.getAccessTokenValidity());
    }
}
