package kr.hjun.backend.controller;

import jakarta.validation.Valid;
import kr.hjun.backend.dto.EmailVerificationConfirmRequest;
import kr.hjun.backend.dto.EmailVerificationSendRequest;
import kr.hjun.backend.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/email-verifications")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    // 인증 메일 발송 요청을 처리한다.
    @PostMapping("/send")
    public ResponseEntity<Void> send(@Valid @RequestBody EmailVerificationSendRequest request) {
        emailVerificationService.sendVerification(request);
        return ResponseEntity.accepted().build();
    }

    // 인증 코드 검증 요청을 처리한다.
    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@Valid @RequestBody EmailVerificationConfirmRequest request) {
        emailVerificationService.verify(request);
        return ResponseEntity.noContent().build();
    }
}
