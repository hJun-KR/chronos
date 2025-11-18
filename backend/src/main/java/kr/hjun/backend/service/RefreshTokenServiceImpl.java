package kr.hjun.backend.service;

import kr.hjun.backend.config.JwtProperties;
import kr.hjun.backend.entity.RefreshToken;
import kr.hjun.backend.entity.User;
import kr.hjun.backend.exception.ChronosException;
import kr.hjun.backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    // 새 리프레시 토큰을 생성한다.
    @Override
    public RefreshToken create(User user) {
        refreshTokenRepository.deleteByUser(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(generateTokenValue())
                .user(user)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtProperties.getRefreshTokenValidity())))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    // 리프레시 토큰을 재발급한다.
    @Override
    public RefreshToken rotate(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new ChronosException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            throw new ChronosException(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다.");
        }

        refreshToken.setToken(generateTokenValue());
        refreshToken.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtProperties.getRefreshTokenValidity())));
        return refreshTokenRepository.save(refreshToken);
    }

    // 리프레시 토큰을 폐기한다.
    @Override
    public void revoke(String tokenValue) {
        refreshTokenRepository.findByToken(tokenValue).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    // 랜덤 토큰 값을 생성한다.
    private String generateTokenValue() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
