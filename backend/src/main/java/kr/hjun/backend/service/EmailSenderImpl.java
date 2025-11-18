package kr.hjun.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(to);
            helper.setFrom(new InternetAddress(fromAddress, true));
            helper.setSubject(subject);
            helper.setText(body, false);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("메일 전송 실패 - 수신자: {}", to, e);
            throw new org.springframework.mail.MailParseException("메일 전송 실패", e);
        }
    }
}
