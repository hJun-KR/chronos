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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmServiceImpl implements AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

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
                .cronExpression(request.getCronExpression())
                .runAt(request.getRunAt())
                .timezone(request.getTimezone())
                .channel(request.getChannel())
                .targetAddress(request.getTargetAddress())
                .build();

        setConditions(alarm, request.getConditions());
        Alarm saved = alarmRepository.save(alarm);
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
        alarm.setCronExpression(request.getCronExpression());
        alarm.setRunAt(request.getRunAt());
        alarm.setTimezone(request.getTimezone());
        alarm.setChannel(request.getChannel());
        alarm.setTargetAddress(request.getTargetAddress());
        alarm.setStatus(request.getStatus());
        alarm.clearConditions();
        setConditions(alarm, request.getConditions());

        return AlarmResponse.from(alarm);
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
}
