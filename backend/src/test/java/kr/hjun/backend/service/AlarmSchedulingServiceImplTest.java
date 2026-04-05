package kr.hjun.backend.service;

import kr.hjun.backend.entity.Alarm;
import kr.hjun.backend.exception.ChronosException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlarmSchedulingServiceImplTest {

    private final AlarmSchedulingService alarmSchedulingService = new AlarmSchedulingServiceImpl();

    // 단발성 알람은 runAt으로 nextRunAt을 설정한다.
    @Test
    @DisplayName("단발성 알람 스케줄 계산")
    void scheduleOnce() {
        Alarm alarm = Alarm.builder()
                .scheduleType(Alarm.ScheduleType.ONCE)
                .runAt(LocalDateTime.now().plusHours(1))
                .timezone("Asia/Seoul")
                .build();

        alarmSchedulingService.updateNextRun(alarm);

        assertThat(alarm.getNextRunAt()).isNotNull();
    }

    // CRON 알람은 다음 실행 시간을 계산한다.
    @Test
    @DisplayName("반복 알람 스케줄 계산")
    void scheduleCron() {
        Alarm alarm = Alarm.builder()
                .scheduleType(Alarm.ScheduleType.RECURRING)
                .cronExpression("0 0 * * * *")
                .timezone("Asia/Seoul")
                .build();

        alarmSchedulingService.updateNextRun(alarm);

        assertThat(alarm.getNextRunAt()).isNotNull();
    }

    // 잘못된 CRON이면 예외를 던진다.
    @Test
    @DisplayName("잘못된 CRON 예외")
    void scheduleInvalidCron() {
        Alarm alarm = Alarm.builder()
                .scheduleType(Alarm.ScheduleType.RECURRING)
                .cronExpression("wrong cron")
                .timezone("Asia/Seoul")
                .build();

        assertThatThrownBy(() -> alarmSchedulingService.updateNextRun(alarm))
                .isInstanceOf(ChronosException.class);
    }
}
