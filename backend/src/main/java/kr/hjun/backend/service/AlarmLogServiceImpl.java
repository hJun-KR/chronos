package kr.hjun.backend.service;

import kr.hjun.backend.dto.AlarmExecutionLogResponse;
import kr.hjun.backend.entity.Alarm;
import kr.hjun.backend.entity.AlarmExecutionLog;
import kr.hjun.backend.exception.ChronosException;
import kr.hjun.backend.repository.AlarmExecutionLogRepository;
import kr.hjun.backend.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmLogServiceImpl implements AlarmLogService {

    private final AlarmRepository alarmRepository;
    private final AlarmExecutionLogRepository alarmExecutionLogRepository;

    @Override
    public List<AlarmExecutionLogResponse> getLogs(Long userId, Long alarmId) {
        Alarm alarm = alarmRepository.findByIdAndUserId(alarmId, userId)
                .orElseThrow(() -> new ChronosException(HttpStatus.NOT_FOUND, "알람을 찾을 수 없습니다."));
        List<AlarmExecutionLog> logs = alarmExecutionLogRepository.findAllByAlarmIdOrderByExecutedAtDesc(alarm.getId());
        return logs.stream().map(AlarmExecutionLogResponse::from).toList();
    }
}
