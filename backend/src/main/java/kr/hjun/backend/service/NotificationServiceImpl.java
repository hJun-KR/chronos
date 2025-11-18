package kr.hjun.backend.service;

import kr.hjun.backend.entity.Alarm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailSender emailSender;

    // 알림을 발송한다.
    @Override
    public void send(Alarm alarm, String payload) {
        if (alarm.getChannel() == Alarm.AlarmChannel.EMAIL) {
            emailSender.send(alarm.getTargetAddress(), "[Chronos] 알림", payload);
            return;
        }
        if (alarm.getChannel() == Alarm.AlarmChannel.DISCORD) {
            log.info("Discord 알림 전송 - url: {}, message: {}", alarm.getTargetAddress(), payload);
            return;
        }
        log.warn("지원하지 않는 채널입니다: {}", alarm.getChannel());
    }
}
