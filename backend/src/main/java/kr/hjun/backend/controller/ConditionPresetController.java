package kr.hjun.backend.controller;

import jakarta.validation.Valid;
import kr.hjun.backend.dto.ConditionPresetRequest;
import kr.hjun.backend.dto.ConditionPresetResponse;
import kr.hjun.backend.security.CustomUserDetails;
import kr.hjun.backend.service.ConditionPresetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/presets")
public class ConditionPresetController {

    private final ConditionPresetService presetService;

    @PostMapping
    public ResponseEntity<ConditionPresetResponse> createPreset(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ConditionPresetRequest request) {
        return ResponseEntity.ok(presetService.createPreset(userDetails.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<ConditionPresetResponse>> getPresets(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(presetService.getPresets(userDetails.getId()));
    }

    @DeleteMapping("/{presetId}")
    public ResponseEntity<Void> deletePreset(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long presetId) {
        presetService.deletePreset(userDetails.getId(), presetId);
        return ResponseEntity.noContent().build();
    }
}
