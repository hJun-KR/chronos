package kr.hjun.backend.service;

import kr.hjun.backend.entity.Alarm;
import kr.hjun.backend.entity.AlarmCondition;
import kr.hjun.backend.entity.AlarmExecutionLog;
import kr.hjun.backend.entity.User;
import kr.hjun.backend.repository.AlarmExecutionLogRepository;
import kr.hjun.backend.repository.AlarmRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmExecutionServiceImplTest {

    @Mock
    private ConditionEvaluationService conditionEvaluationService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AlarmSchedulingService alarmSchedulingService;

    @Mock
    private AlarmExecutionLogRepository executionLogRepository;

    @Mock
    private AlarmRepository alarmRepository;

    @InjectMocks
    private AlarmExecutionServiceImpl alarmExecutionService;

    // 조건 충족 시 알림을 전송하고 로그를 저장한다.
    @Test
    @DisplayName("알람 실행 성공")
    void executeSuccess() {
        Alarm alarm = sampleAlarm();
        when(conditionEvaluationService.evaluate(eq(alarm.getConditions()), any())).thenReturn(true);

        alarmExecutionService.execute(alarm);

        verify(notificationService).send(eq(alarm), anyString());
        verify(executionLogRepository).save(any(AlarmExecutionLog.class));
        verify(alarmSchedulingService).updateNextRun(alarm);
    }

    // 조건 불충족 시 알림을 건너뛴다.
    @Test
    @DisplayName("알람 실행 - 조건 미충족")
    void executeSkip() {
        Alarm alarm = sampleAlarm();
        when(conditionEvaluationService.evaluate(eq(alarm.getConditions()), any())).thenReturn(false);

        alarmExecutionService.execute(alarm);

        verify(notificationService, never()).send(any(), any());
        verify(executionLogRepository, never()).save(any());
    }

    private Alarm sampleAlarm() {
        AlarmCondition condition = AlarmCondition.builder()
                .conditionType(AlarmCondition.ConditionType.WEATHER)
                .operator(AlarmCondition.Operator.EQ)
                .fieldKey("temp")
                .fieldValue("10")
                .build();
        Alarm alarm = Alarm.builder()
                .id(1L)
                .user(User.builder().id(1L).build())
                .name("테스트")
                .alarmType(Alarm.AlarmType.TIME)
                .scheduleType(Alarm.ScheduleType.ONCE)
                .runAt(LocalDateTime.now().plusMinutes(5))
                .timezone("Asia/Seoul")
                .channel(Alarm.AlarmChannel.EMAIL)
                .targetAddress("test@example.com")
                .build();
        alarm.addCondition(condition);
        return alarm;
    }
}
