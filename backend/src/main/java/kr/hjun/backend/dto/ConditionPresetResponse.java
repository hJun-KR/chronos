package kr.hjun.backend.dto;

import kr.hjun.backend.entity.ConditionPreset;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConditionPresetResponse {
    private Long id;
    private String name;
    private String description;
    private String conditionsJson;

    public static ConditionPresetResponse from(ConditionPreset preset) {
        return ConditionPresetResponse.builder()
                .id(preset.getId())
                .name(preset.getName())
                .description(preset.getDescription())
                .conditionsJson(preset.getConditionsJson())
                .build();
    }
}
