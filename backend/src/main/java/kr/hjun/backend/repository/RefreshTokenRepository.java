package kr.hjun.backend.repository;

import kr.hjun.backend.entity.RefreshToken;
import kr.hjun.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 토큰 문자열로 조회한다.
    Optional<RefreshToken> findByToken(String token);

    // 사용자 토큰을 삭제한다.
    void deleteByUser(User user);
}
