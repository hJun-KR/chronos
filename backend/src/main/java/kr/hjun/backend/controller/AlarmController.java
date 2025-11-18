package kr.hjun.backend.controller;

import jakarta.validation.Valid;
import kr.hjun.backend.dto.AlarmCreateRequest;
import kr.hjun.backend.dto.AlarmResponse;
import kr.hjun.backend.dto.AlarmUpdateRequest;
import kr.hjun.backend.security.CustomUserDetails;
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
}
