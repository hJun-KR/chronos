package kr.hjun.backend.service;

import kr.hjun.backend.entity.RefreshToken;
import kr.hjun.backend.entity.User;

public interface RefreshTokenService {

    // 새 리프레시 토큰을 생성한다.
    RefreshToken create(User user);

    // 리프레시 토큰을 재발급한다.
    RefreshToken rotate(String tokenValue);

    // 리프레시 토큰을 폐기한다.
    void revoke(String tokenValue);
}
