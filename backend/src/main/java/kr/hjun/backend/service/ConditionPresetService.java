package kr.hjun.backend.service;

import kr.hjun.backend.dto.ConditionPresetRequest;
import kr.hjun.backend.dto.ConditionPresetResponse;

import java.util.List;

public interface ConditionPresetService {
    ConditionPresetResponse createPreset(Long userId, ConditionPresetRequest request);
    List<ConditionPresetResponse> getPresets(Long userId);
    void deletePreset(Long userId, Long presetId);
}
