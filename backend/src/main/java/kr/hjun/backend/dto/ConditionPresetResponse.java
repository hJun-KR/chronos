package kr.hjun.backend.dto;

import java.util.List;

public record ConditionPresetResponse(
        String key,
        String name,
        String description,
        List<AlarmConditionRequest> conditions
) {
}
