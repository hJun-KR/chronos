package kr.hjun.backend.service;

import  kr.hjun.backend.dto.AlarmCreateRequest;
import kr.hjun.backend.dto.AlarmResponse;
import kr.hjun.backend.dto.AlarmUpdateRequest;

import java.util.List;

public interface AlarmService {

    // 알람을 생성한다.
    AlarmResponse createAlarm(Long userId, AlarmCreateRequest request);

    // 알람을 수정한다.
    AlarmResponse updateAlarm(Long userId, Long alarmId, AlarmUpdateRequest request);

    // 알람을 조회한다.
    AlarmResponse getAlarm(Long userId, Long alarmId);

    // 사용자 알람 목록을 조회한다.
    List<AlarmResponse> getAlarms(Long userId);

    // 알람을 삭제한다.
    void deleteAlarm(Long userId, Long alarmId);
}

