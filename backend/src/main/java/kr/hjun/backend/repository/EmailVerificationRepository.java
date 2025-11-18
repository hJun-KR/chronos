package kr.hjun.backend.repository;

import kr.hjun.backend.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    // 이메일로 최신 인증 정보를 조회한다.
    Optional<EmailVerification> findTopByEmailOrderByCreatedAtDesc(String email);

    // 이메일과 코드로 조회한다.
    Optional<EmailVerification> findTopByEmailAndCodeOrderByCreatedAtDesc(String email, String code);
}
