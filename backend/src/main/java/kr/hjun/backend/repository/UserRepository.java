package kr.hjun.backend.repository;

import kr.hjun.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일 사용자 조회
    Optional<User> findByEmail(String email);

    // 이메일 중복 확인
    boolean existsByEmail(String email);

    // 활성 사용자 조회
    Optional<User> findByEmailAndIsActiveTrue(String email);

    // 탈퇴 계정 확인
    boolean existsByEmailAndIsActiveFalse(String email);
}
