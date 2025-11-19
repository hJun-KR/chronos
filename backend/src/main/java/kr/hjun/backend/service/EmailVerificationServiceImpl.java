package kr.hjun.backend.service;

import kr.hjun.backend.dto.EmailVerificationConfirmRequest;
import kr.hjun.backend.dto.EmailVerificationSendRequest;
import kr.hjun.backend.entity.EmailVerification;
import kr.hjun.backend.entity.User;
import kr.hjun.backend.exception.ChronosException;
import kr.hjun.backend.repository.EmailVerificationRepository;
import kr.hjun.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 10;

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final EmailSender emailSender;
    private final Random random = new Random();

    // 신규 사용자에게 인증 메일을 보낸다.
    @Override
    public void sendVerification(User user) {
        String code = generateCode();
        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .email(user.getEmail())
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES))
                .verified(false)
                .build();
        emailVerificationRepository.save(verification);
        emailSender.send(user.getEmail(), "Chronos 이메일 인증 코드", buildMailBody(code));
    }

    // 이메일로 인증 메일을 보낸다.
    @Override
    public void sendVerification(EmailVerificationSendRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        String code = generateCode();
        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .email(request.getEmail())
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES))
                .verified(false)
                .build();
        emailVerificationRepository.save(verification);
        emailSender.send(request.getEmail(), "Chronos 이메일 인증 코드", buildMailBody(code));
    }

    // 인증 코드를 검증한다.
    @Override
    public void verify(EmailVerificationConfirmRequest request) {
        EmailVerification verification = emailVerificationRepository
                .findTopByEmailAndCodeOrderByCreatedAtDesc(request.getEmail(), request.getCode())
                .orElseThrow(() -> new ChronosException(HttpStatus.BAD_REQUEST, "인증 정보를 찾을 수 없습니다."));

        if (verification.isExpired()) {
            throw new ChronosException(HttpStatus.BAD_REQUEST, "인증 코드가 만료되었습니다.");
        }

        verification.setVerified(true);
        if (verification.getUser() != null) {
            verification.getUser().setActive(true);
        }
    }

    // 인증 메일 본문을 생성한다.
    private String buildMailBody(String code) {
        return "Chronos 인증 코드는 %s 입니다. %d분 이내에 입력해 주세요.".formatted(code, EXPIRATION_MINUTES);
    }

    // 랜덤 인증 코드를 생성한다.
    private String generateCode() {
        int value = random.nextInt((int) Math.pow(10, CODE_LENGTH));
        return String.format("%0" + CODE_LENGTH + "d", value);
    }
}
