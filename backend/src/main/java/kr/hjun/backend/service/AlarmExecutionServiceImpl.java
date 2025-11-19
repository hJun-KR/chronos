package kr.hjun.backend.service;

import kr.hjun.backend.dto.AlarmSimulationResponse;
import kr.hjun.backend.entity.Alarm;
import kr.hjun.backend.entity.AlarmExecutionLog;
import kr.hjun.backend.exception.ChronosException;
import kr.hjun.backend.repository.AlarmRepository;
import kr.hjun.backend.repository.AlarmExecutionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmExecutionServiceImpl implements AlarmExecutionService {

    private final ConditionEvaluationService conditionEvaluationService;
    private final ConditionContextProvider conditionContextProvider;
    private final NotificationService notificationService;
    private final AlarmSchedulingService alarmSchedulingService;
    private final AlarmExecutionLogRepository executionLogRepository;
    private final AlarmRepository alarmRepository;

    // 알람을 실행한다.
    @Override
    @Transactional
    public void execute(Alarm alarm) {
        Alarm managed = alarmRepository.findById(alarm.getId())
                .orElseThrow(() -> new ChronosException(HttpStatus.NOT_FOUND, "알람을 찾을 수 없습니다."));
        ConditionContext context = conditionContextProvider.buildContext(managed);
        boolean shouldRun = conditionEvaluationService.evaluate(managed.getConditions(), context);
        if (!shouldRun) {
            log.info("조건이 충족되지 않아 알람 실행을 건너뜀: {}", managed.getId());
            updateScheduling(managed);
            return;
        }

        try {
            notificationService.send(managed, buildPayload(managed));
            recordLog(managed, true, "SUCCESS", null);
        } catch (Exception e) {
            recordLog(managed, false, "FAILED", e.getMessage());
        } finally {
            updateScheduling(managed);
        }
    }

    // 사용자 입력 컨텍스트로 시뮬레이션한다.
    @Override
    public AlarmSimulationResponse simulate(Alarm alarm, ConditionContext context, boolean sendNotification) {
        ConditionContext evaluatedContext = context != null ? context : conditionContextProvider.buildContext(alarm);
        boolean shouldRun = conditionEvaluationService.evaluate(alarm.getConditions(), evaluatedContext);
        if (!shouldRun) {
            return new AlarmSimulationResponse(false, false, "조건이 충족되지 않았습니다.");
        }

        boolean sent = false;
        if (sendNotification) {
            notificationService.send(alarm, buildPayload(alarm));
            sent = true;
        }
        return new AlarmSimulationResponse(true, sent, sent ? "알림 전송 완료" : "조건 충족 - 전송 안 함");
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

    private void updateScheduling(Alarm alarm) {
        alarmSchedulingService.updateNextRun(alarm);
        alarmRepository.save(alarm);
    }

    private String buildPayload(Alarm alarm) {
        return "알람 [" + alarm.getName() + "] 이 실행되었습니다.";
    }
}
