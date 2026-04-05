package kr.hjun.backend.service;

import kr.hjun.backend.dto.EmailVerificationConfirmRequest;
import kr.hjun.backend.dto.EmailVerificationSendRequest;
import kr.hjun.backend.entity.EmailVerification;
import kr.hjun.backend.entity.User;
import kr.hjun.backend.repository.EmailVerificationRepository;
import kr.hjun.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({EmailVerificationServiceImpl.class, EmailVerificationServiceImplTest.EmailSenderTestConfig.class})
class EmailVerificationServiceImplTest {

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private UserRepository userRepository;

    @TestConfiguration
    static class EmailSenderTestConfig {

        // 테스트용 이메일 발송기를 제공한다.
        @Bean
        public EmailSender emailSender() {
            return (to, subject, body) -> {
            };
        }
    }

    // 사용자의 이메일 인증을 저장한다.
    @Test
    @DisplayName("이메일 인증 발송")
    void sendVerification() {
        EmailVerificationSendRequest request = new EmailVerificationSendRequest();
        request.setEmail("send@example.com");

        emailVerificationService.sendVerification(request);

        assertThat(emailVerificationRepository.findTopByEmailOrderByCreatedAtDesc("send@example.com")).isPresent();
    }

    // 인증 성공 시 사용자가 활성화된다.
    @Test
    @DisplayName("이메일 인증 성공 시 활성화")
    void verify() {
        User user = User.builder()
                .email("verify@example.com")
                .name("사용자")
                .password("encoded")
                .role(User.Role.USER)
                .isActive(false)
                .build();
        userRepository.save(user);

        EmailVerification verification = EmailVerification.builder()
                .email(user.getEmail())
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .user(user)
                .build();
        emailVerificationRepository.save(verification);

        EmailVerificationConfirmRequest request = new EmailVerificationConfirmRequest();
        request.setEmail(user.getEmail());
        request.setCode("123456");

        emailVerificationService.verify(request);

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.isActive()).isTrue();
    }
}
