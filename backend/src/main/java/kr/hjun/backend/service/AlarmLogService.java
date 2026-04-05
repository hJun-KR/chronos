package kr.hjun.backend.service;

import kr.hjun.backend.dto.AlarmExecutionLogResponse;

import java.util.List;

public interface AlarmLogService {

    List<AlarmExecutionLogResponse> getLogs(Long userId, Long alarmId);
}
