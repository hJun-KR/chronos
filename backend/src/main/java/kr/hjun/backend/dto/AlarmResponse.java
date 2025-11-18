package kr.hjun.backend.dto;

import kr.hjun.backend.entity.Alarm;

import java.time.LocalDateTime;
import java.util.List;

public record AlarmResponse(
        Long id,
        String name,
        String description,
        Alarm.AlarmType alarmType,
        Alarm.ScheduleType scheduleType,
        String cronExpression,
        LocalDateTime runAt,
        String timezone,
        Alarm.AlarmChannel channel,
        String targetAddress,
        Alarm.AlarmStatus status,
        LocalDateTime lastRunAt,
        LocalDateTime nextRunAt,
        String lastResult,
        List<AlarmConditionResponse> conditions
) {

    public static AlarmResponse from(Alarm alarm) {
        return new AlarmResponse(
                alarm.getId(),
                alarm.getName(),
                alarm.getDescription(),
                alarm.getAlarmType(),
                alarm.getScheduleType(),
                alarm.getCronExpression(),
                alarm.getRunAt(),
                alarm.getTimezone(),
                alarm.getChannel(),
                alarm.getTargetAddress(),
                alarm.getStatus(),
                alarm.getLastRunAt(),
                alarm.getNextRunAt(),
                alarm.getLastResult(),
                alarm.getConditions().stream()
                        .map(AlarmConditionResponse::from)
                        .toList()
        );
    }
}
