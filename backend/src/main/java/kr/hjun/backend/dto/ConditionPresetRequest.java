package kr.hjun.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionPresetRequest {
    @NotBlank(message = "프리셋 이름을 입력하세요.")
    private String name;
    private String description;
    @NotBlank(message = "조건 JSON은 비어있을 수 없습니다.")
    private String conditionsJson;
}
