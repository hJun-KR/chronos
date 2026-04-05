package kr.hjun.backend.service;

import kr.hjun.backend.dto.AlarmConditionRequest;
import kr.hjun.backend.dto.AlarmCreateRequest;
import kr.hjun.backend.dto.AlarmResponse;
import kr.hjun.backend.dto.AlarmUpdateRequest;
import kr.hjun.backend.entity.Alarm;
import kr.hjun.backend.entity.AlarmCondition;
import kr.hjun.backend.entity.User;
import kr.hjun.backend.exception.ChronosException;
import kr.hjun.backend.repository.AlarmRepository;
import kr.hjun.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmServiceImpl implements AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final AlarmSchedulingService alarmSchedulingService;
    private final org.springframework.beans.factory.ObjectProvider<kr.hjun.backend.scheduler.AlarmScheduler> alarmSchedulerProvider;

    // 알람을 생성한다.
    @Override
    public AlarmResponse createAlarm(Long userId, AlarmCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ChronosException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Alarm alarm = Alarm.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .alarmType(request.getAlarmType())
                .scheduleType(request.getScheduleType())
                .timezone(request.getTimezone())
                .channel(request.getChannel())
                .targetAddress(request.getTargetAddress())
                .build();

        applyScheduleSettings(alarm,
                request.getRunAt(),
                request.getCronExpression(),
                request.getRecurrenceType(),
                request.getDaysOfWeek(),
                request.getDayOfMonth(),
                request.getMonthOfYear());
        setConditions(alarm, request.getConditions());
        alarmSchedulingService.updateNextRun(alarm);
        Alarm saved = alarmRepository.save(alarm);
        alarmSchedulerProvider.ifAvailable(scheduler -> scheduler.schedule(saved));
        return AlarmResponse.from(saved);
    }

    // 알람을 수정한다.
    @Override
    public AlarmResponse updateAlarm(Long userId, Long alarmId, AlarmUpdateRequest request) {
        Alarm alarm = getOwnedAlarm(userId, alarmId);

        alarm.setName(request.getName());
        alarm.setDescription(request.getDescription());
        alarm.setAlarmType(request.getAlarmType());
        alarm.setScheduleType(request.getScheduleType());
        alarm.setTimezone(request.getTimezone());
        alarm.setChannel(request.getChannel());
        alarm.setTargetAddress(request.getTargetAddress());
        alarm.setStatus(request.getStatus());
        applyScheduleSettings(alarm,
                request.getRunAt(),
                request.getCronExpression(),
                request.getRecurrenceType(),
                request.getDaysOfWeek(),
                request.getDayOfMonth(),
                request.getMonthOfYear());
        alarm.clearConditions();
        setConditions(alarm, request.getConditions());
        alarmSchedulingService.updateNextRun(alarm);

        AlarmResponse response = AlarmResponse.from(alarm);
        alarmSchedulerProvider.ifAvailable(scheduler -> scheduler.schedule(alarm));
        return response;
    }

    // 알람을 조회한다.
    @Transactional(readOnly = true)
    @Override
    public AlarmResponse getAlarm(Long userId, Long alarmId) {
        Alarm alarm = getOwnedAlarm(userId, alarmId);
        return AlarmResponse.from(alarm);
    }

    // 알람 목록을 조회한다.
    @Transactional(readOnly = true)
    @Override
    public List<AlarmResponse> getAlarms(Long userId) {
        return alarmRepository.findAllByUserId(userId)
                .stream()
                .map(AlarmResponse::from)
                .collect(Collectors.toList());
    }

    // 알람을 삭제한다.
    @Override
    public void deleteAlarm(Long userId, Long alarmId) {
        Alarm alarm = getOwnedAlarm(userId, alarmId);
        alarmRepository.delete(alarm);
        alarmSchedulerProvider.ifAvailable(scheduler -> scheduler.cancel(alarmId));
    }

    // 요청 조건을 엔티티로 반영한다.
    private void setConditions(Alarm alarm, List<AlarmConditionRequest> requests) {
        requests.forEach(conditionRequest -> {
            AlarmCondition condition = AlarmCondition.builder()
                    .conditionType(conditionRequest.getConditionType())
                    .operator(conditionRequest.getOperator())
                    .fieldKey(conditionRequest.getFieldKey())
                    .fieldValue(conditionRequest.getFieldValue())
                    .extraJson(conditionRequest.getExtraJson())
                    .build();
            alarm.addCondition(condition);
        });
    }

    // 사용자 소유 알람을 조회한다.
    private Alarm getOwnedAlarm(Long userId, Long alarmId) {
        return alarmRepository.findByIdAndUserId(alarmId, userId)
                .orElseThrow(() -> new ChronosException(HttpStatus.NOT_FOUND, "알람을 찾을 수 없습니다."));
    }

    private void applyScheduleSettings(Alarm alarm,
                                       LocalDateTime runAt,
                                       String cronExpression,
                                       Alarm.RecurrenceType recurrenceType,
                                       Set<DayOfWeek> daysOfWeek,
                                       Integer dayOfMonth,
                                       Integer monthOfYear) {
        alarm.setRunAt(runAt);
        alarm.setRecurrenceType(recurrenceType);
        alarm.setRecurrenceDays(formatDays(daysOfWeek));
        alarm.setRecurrenceDayOfMonth(dayOfMonth);
        alarm.setRecurrenceMonthOfYear(monthOfYear);
        alarm.setCronExpression(resolveCronExpression(alarm, cronExpression, runAt));
    }

    private String formatDays(Collection<DayOfWeek> days) {
        if (days == null || days.isEmpty()) {
            return null;
        }
        return days.stream()
                .map(DayOfWeek::name)
                .collect(Collectors.joining(","));
    }

    private String resolveCronExpression(Alarm alarm, String cronExpression, LocalDateTime runAt) {
        if (alarm.getScheduleType() == Alarm.ScheduleType.ONCE) {
            return null;
        }

        if (cronExpression != null && !cronExpression.isBlank()) {
            String trimmed = cronExpression.trim();
            try {
                org.springframework.scheduling.support.CronExpression.parse(trimmed);
                return trimmed;
            } catch (IllegalArgumentException e) {
                throw new ChronosException(HttpStatus.BAD_REQUEST, "유효하지 않은 CRON 표현식입니다: " + trimmed);
            }
        }

        if (runAt == null) {
            throw new ChronosException(HttpStatus.BAD_REQUEST, "반복 알람은 실행 시각(runAt) 혹은 cronExpression이 필요합니다.");
        }

        Alarm.RecurrenceType recurrenceType = alarm.getRecurrenceType();
        if (recurrenceType == null) {
            throw new ChronosException(HttpStatus.BAD_REQUEST, "반복 유형을 지정하거나 cronExpression을 입력해야 합니다.");
        }

        String minute = String.valueOf(runAt.getMinute());
        String hour = String.valueOf(runAt.getHour());

        return switch (recurrenceType) {
            case DAILY -> "0 " + minute + " " + hour + " * * ?";
            case WEEKLY -> buildWeeklyCron(alarm, hour, minute);
            case MONTHLY -> buildMonthlyCron(alarm, hour, minute);
            case YEARLY -> buildYearlyCron(alarm, hour, minute);
        };
    }

    private String buildWeeklyCron(Alarm alarm, String hour, String minute) {
        String days = alarm.getRecurrenceDays();
        if (days == null || days.isBlank()) {
            throw new ChronosException(HttpStatus.BAD_REQUEST, "요일을 최소 한 개 이상 선택해야 합니다.");
        }
        String cronDays = java.util.Arrays.stream(days.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .map(DayOfWeek::valueOf)
                .map(day -> day.name().substring(0, 3))
                .collect(Collectors.joining(","));
        return "0 " + minute + " " + hour + " ? * " + cronDays;
    }

    private String buildMonthlyCron(Alarm alarm, String hour, String minute) {
        Integer day = alarm.getRecurrenceDayOfMonth();
        if (day == null || day < 1 || day > 31) {
            throw new ChronosException(HttpStatus.BAD_REQUEST, "월 반복 시 일(dayOfMonth)을 지정해야 합니다.");
        }
        return "0 " + minute + " " + hour + " " + day + " * ?";
    }

    private String buildYearlyCron(Alarm alarm, String hour, String minute) {
        Integer day = alarm.getRecurrenceDayOfMonth();
        Integer month = alarm.getRecurrenceMonthOfYear();
        if (day == null || month == null || day < 1 || day > 31 || month < 1 || month > 12) {
            throw new ChronosException(HttpStatus.BAD_REQUEST, "연간 반복 시 월/일을 모두 지정해야 합니다.");
        }
        return "0 " + minute + " " + hour + " " + day + " " + month + " ?";
    }
}
