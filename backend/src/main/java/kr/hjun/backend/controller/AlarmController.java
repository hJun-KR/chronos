package kr.hjun.backend.controller;

import jakarta.validation.Valid;
import kr.hjun.backend.dto.*;
import kr.hjun.backend.dto.AlarmExecutionLogResponse;
import kr.hjun.backend.entity.Alarm;
import kr.hjun.backend.repository.AlarmRepository;
import kr.hjun.backend.security.CustomUserDetails;
import kr.hjun.backend.service.AlarmExecutionService;
import kr.hjun.backend.service.AlarmLogService;
import kr.hjun.backend.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarms")
public class AlarmController {

    private final AlarmService alarmService;
    private final AlarmExecutionService alarmExecutionService;
    private final AlarmLogService alarmLogService;
    private final AlarmRepository alarmRepository;

    // 알람을 생성한다.
    @PostMapping
    public ResponseEntity<AlarmResponse> createAlarm(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @Valid @RequestBody AlarmCreateRequest request) {
        AlarmResponse response = alarmService.createAlarm(userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }

    // 알람을 수정한다.
    @PutMapping("/{alarmId}")
    public ResponseEntity<AlarmResponse> updateAlarm(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @PathVariable Long alarmId,
                                                     @Valid @RequestBody AlarmUpdateRequest request) {
        AlarmResponse response = alarmService.updateAlarm(userDetails.getId(), alarmId, request);
        return ResponseEntity.ok(response);
    }

    // 알람을 조회한다.
    @GetMapping("/{alarmId}")
    public ResponseEntity<AlarmResponse> getAlarm(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable Long alarmId) {
        AlarmResponse response = alarmService.getAlarm(userDetails.getId(), alarmId);
        return ResponseEntity.ok(response);
    }

    // 알람 목록을 조회한다.
    @GetMapping
    public ResponseEntity<List<AlarmResponse>> getAlarms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<AlarmResponse> responses = alarmService.getAlarms(userDetails.getId());
        return ResponseEntity.ok(responses);
    }

    // 알람을 삭제한다.
    @DeleteMapping("/{alarmId}")
    public ResponseEntity<Void> deleteAlarm(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable Long alarmId) {
        alarmService.deleteAlarm(userDetails.getId(), alarmId);
        return ResponseEntity.noContent().build();
    }

    // 알람을 시뮬레이션한다.
    @PostMapping("/{alarmId}/simulate")
    public ResponseEntity<AlarmSimulationResponse> simulate(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable Long alarmId,
                                                            @RequestBody AlarmSimulationRequest request) {
        Alarm alarm = alarmRepository.findByIdAndUserId(alarmId, userDetails.getId())
                .orElseThrow(() -> new kr.hjun.backend.exception.ChronosException(org.springframework.http.HttpStatus.NOT_FOUND, "알람을 찾을 수 없습니다."));
        AlarmSimulationResponse response = alarmExecutionService.simulate(alarm, request.toContext(), request.sendNotification());
        return ResponseEntity.ok(response);
    }

    // 알람을 즉시 실행한다.
    @PostMapping("/{alarmId}/run-now")
    public ResponseEntity<Void> runNow(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @PathVariable Long alarmId) {
        Alarm alarm = alarmRepository.findByIdAndUserId(alarmId, userDetails.getId())
                .orElseThrow(() -> new kr.hjun.backend.exception.ChronosException(org.springframework.http.HttpStatus.NOT_FOUND, "알람을 찾을 수 없습니다."));
        alarmExecutionService.execute(alarm);
        return ResponseEntity.accepted().build();
    }

    // 알람 실행 로그를 조회한다.
    @GetMapping("/{alarmId}/logs")
    public ResponseEntity<List<AlarmExecutionLogResponse>> getLogs(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                   @PathVariable Long alarmId) {
        List<AlarmExecutionLogResponse> logs = alarmLogService.getLogs(userDetails.getId(), alarmId);
        return ResponseEntity.ok(logs);
    }
}
