package kr.hjun.backend.dto;

import kr.hjun.backend.entity.AlarmExecutionLog;

import java.time.LocalDateTime;

public record AlarmExecutionLogResponse(
        Long id,
        LocalDateTime executedAt,
        boolean success,
        String message,
        String payloadSnapshot
) {

    public static AlarmExecutionLogResponse from(AlarmExecutionLog log) {
        return new AlarmExecutionLogResponse(
                log.getId(),
                log.getExecutedAt(),
                log.isSuccess(),
                log.getMessage(),
                log.getPayloadSnapshot()
        );
    }
}
