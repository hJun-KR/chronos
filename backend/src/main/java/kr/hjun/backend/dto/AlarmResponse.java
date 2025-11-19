package kr.hjun.backend.dto;

import kr.hjun.backend.entity.Alarm;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        List<AlarmConditionResponse> conditions,
        Alarm.RecurrenceType recurrenceType,
        Set<java.time.DayOfWeek> daysOfWeek,
        Integer dayOfMonth,
        Integer monthOfYear
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
                        .toList(),
                alarm.getRecurrenceType(),
                parseDays(alarm.getRecurrenceDays()),
                alarm.getRecurrenceDayOfMonth(),
                alarm.getRecurrenceMonthOfYear()
        );
    }

    private static Set<java.time.DayOfWeek> parseDays(String raw) {
        if (raw == null || raw.isBlank()) {
            return Set.of();
        }
        return java.util.Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .map(java.time.DayOfWeek::valueOf)
                .collect(Collectors.toSet());
    }
}
