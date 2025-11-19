package kr.hjun.backend.service;

import kr.hjun.backend.dto.ConditionPresetResponse;

import java.util.List;

public interface ConditionPresetService {

    List<ConditionPresetResponse> getPresets();

    ConditionPresetResponse getPreset(String key);
}
