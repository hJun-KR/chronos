package kr.hjun.backend.controller;

import kr.hjun.backend.dto.ConditionPresetResponse;
import kr.hjun.backend.service.ConditionPresetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/condition-presets")
public class ConditionPresetController {

    private final ConditionPresetService conditionPresetService;

    @GetMapping
    public ResponseEntity<List<ConditionPresetResponse>> getPresets() {
        return ResponseEntity.ok(conditionPresetService.getPresets());
    }

    @GetMapping("/{key}")
    public ResponseEntity<ConditionPresetResponse> getPreset(@PathVariable String key) {
        return ResponseEntity.ok(conditionPresetService.getPreset(key));
    }
}
