package kr.hjun.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    @Value("${MAILER_FROM:no-reply@chronos.local}")
    private String fromAddress;

    // 이메일을 발송한다.
    @Override
    public void send(String to, String subject, String body) {
        JavaMailSender javaMailSender = mailSenderProvider.getIfAvailable();
        if (javaMailSender == null) {
            log.info("메일 전송 미구현 - 수신자: {}, 제목: {}, 본문: {}", to, subject, body);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress.replace("\"", ""));
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }
}
