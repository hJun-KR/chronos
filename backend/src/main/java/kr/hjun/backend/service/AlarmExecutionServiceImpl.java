package kr.hjun.backend.service;

import kr.hjun.backend.entity.Alarm;
import kr.hjun.backend.entity.AlarmExecutionLog;
import kr.hjun.backend.repository.AlarmRepository;
import kr.hjun.backend.repository.AlarmExecutionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmExecutionServiceImpl implements AlarmExecutionService {

    private final ConditionEvaluationService conditionEvaluationService;
    private final NotificationService notificationService;
    private final AlarmSchedulingService alarmSchedulingService;
    private final AlarmExecutionLogRepository executionLogRepository;
    private final AlarmRepository alarmRepository;

    // 알람을 실행한다.
    @Override
    @Transactional
    public void execute(Alarm alarm) {
        boolean shouldRun = conditionEvaluationService.evaluate(alarm.getConditions(), ConditionContext.empty());
        if (!shouldRun) {
            log.info("조건이 충족되지 않아 알람 실행을 건너뜀: {}", alarm.getId());
            updateScheduling(alarm, "SKIPPED");
            return;
        }

        try {
            notificationService.send(alarm, buildPayload(alarm));
            recordLog(alarm, true, "SUCCESS", null);
        } catch (Exception e) {
            recordLog(alarm, false, "FAILED", e.getMessage());
        } finally {
            updateScheduling(alarm, alarm.getLastResult());
        }
    }

    private void recordLog(Alarm alarm, boolean success, String message, String payload) {
        AlarmExecutionLog logEntry = AlarmExecutionLog.builder()
                .alarm(alarm)
                .executedAt(LocalDateTime.now())
                .success(success)
                .message(message)
                .payloadSnapshot(payload)
                .build();
        executionLogRepository.save(logEntry);
        alarm.setLastRunAt(logEntry.getExecutedAt());
        alarm.setLastResult(message);
    }

    private void updateScheduling(Alarm alarm, String result) {
        alarmSchedulingService.updateNextRun(alarm);
        alarmRepository.save(alarm);
    }

    private String buildPayload(Alarm alarm) {
        return "알람 [" + alarm.getName() + "] 이 실행되었습니다.";
    }
}
