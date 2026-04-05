package kr.hjun.backend.service;

import kr.hjun.backend.entity.Alarm;
import kr.hjun.backend.exception.ChronosException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class AlarmSchedulingServiceImpl implements AlarmSchedulingService {

    // 알람의 다음 실행 시간을 계산한다.
    @Override
    public void updateNextRun(Alarm alarm) {
        ZoneId zoneId = ZoneId.of(alarm.getTimezone());
        if (alarm.getScheduleType() == Alarm.ScheduleType.ONCE) {
            LocalDateTime runAt = alarm.getRunAt();
            if (runAt == null) {
                throw new ChronosException(HttpStatus.BAD_REQUEST, "단발성 알람은 runAt이 필요합니다.");
            }
            LocalDateTime now = LocalDateTime.now(zoneId);
            alarm.setNextRunAt(runAt.isAfter(now) ? runAt : null);
            return;
        }

        String cron = alarm.getCronExpression();
        if (cron == null || cron.isBlank()) {
            throw new ChronosException(HttpStatus.BAD_REQUEST, "반복 알람은 cronExpression이 필요합니다.");
        }

        try {
            CronExpression cronExpression = CronExpression.parse(cron);
            ZonedDateTime next = cronExpression.next(ZonedDateTime.now(zoneId));
            alarm.setNextRunAt(next != null ? next.toLocalDateTime() : null);
        } catch (IllegalArgumentException e) {
            throw new ChronosException(HttpStatus.BAD_REQUEST, "유효하지 않은 CRON 표현식입니다.");
        }
    }
}
