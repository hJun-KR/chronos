package kr.hjun.backend.service;

import kr.hjun.backend.entity.Alarm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailSender emailSender;
    private final RestTemplate restTemplate;

    // 알림을 발송한다.
    @Override
    public void send(Alarm alarm, String payload) {
        if (alarm.getChannel() == Alarm.AlarmChannel.EMAIL) {
            emailSender.send(alarm.getTargetAddress(), "[Chronos] 알림", payload);
            return;
        }
        if (alarm.getChannel() == Alarm.AlarmChannel.DISCORD) {
            sendDiscord(alarm.getTargetAddress(), payload);
            return;
        }
        log.warn("지원하지 않는 채널입니다: {}", alarm.getChannel());
    }

    private void sendDiscord(String webhookUrl, String payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String json = "{\"content\":\"" + payload.replace("\"", "\\\"") + "\"}";
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            restTemplate.postForEntity(webhookUrl, entity, String.class);
        } catch (Exception e) {
            log.error("Discord 알림 전송 실패", e);
            throw new kr.hjun.backend.exception.ChronosException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Discord 알림 전송 실패");
        }
    }
}
