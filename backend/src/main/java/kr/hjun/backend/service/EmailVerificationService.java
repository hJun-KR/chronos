package kr.hjun.backend.service;

import kr.hjun.backend.dto.EmailVerificationConfirmRequest;
import kr.hjun.backend.dto.EmailVerificationSendRequest;
import kr.hjun.backend.entity.User;

public interface EmailVerificationService {

    // 신규 사용자에게 인증 메일을 보낸다.
    void sendVerification(User user);

    // 이메일로 인증 메일을 보낸다.
    void sendVerification(EmailVerificationSendRequest request);

    // 인증 코드를 검증한다.
    void verify(EmailVerificationConfirmRequest request);
}
