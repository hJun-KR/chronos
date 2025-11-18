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
            InternetAddress parsedFrom = parseFromAddress(fromAddress);
            helper.setFrom(parsedFrom);
            helper.setSubject(subject);
            helper.setText(body, false);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("메일 전송 실패 - 수신자: {}", to, e);
            throw new org.springframework.mail.MailParseException("메일 전송 실패", e);
        }
    }

    // 환경 변수에서 전달된 발신 주소를 파싱한다.
    private InternetAddress parseFromAddress(String raw) throws MessagingException {
        String address = raw;
        String personal = null;
        if (raw.contains("<")) {
            int start = raw.indexOf('<');
            int end = raw.indexOf('>', start);
            if (end == -1) {
                end = raw.length();
            }
            String maybeAddress = raw.substring(start + 1, end).trim();
            if (!maybeAddress.isEmpty()) {
                address = maybeAddress;
            }
            personal = raw.substring(0, start).replace("\"", "").trim();
        } else {
            address = address.replace("\"", "").trim();
        }
        address = address.replace("<", "").replace(">", "").trim();
        InternetAddress internetAddress = new InternetAddress(address);
        if (personal != null && !personal.isEmpty()) {
            try {
                internetAddress.setPersonal(personal, "UTF-8");
            } catch (java.io.UnsupportedEncodingException e) {
                throw new MessagingException("발신자명을 인코딩할 수 없습니다.", e);
            }
        }
        return internetAddress;
    }
}
