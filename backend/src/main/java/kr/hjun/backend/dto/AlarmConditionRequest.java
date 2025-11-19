package kr.hjun.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.hjun.backend.entity.AlarmCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmConditionRequest {

    @NotNull(message = "조건 유형은 필수입니다.")
    private AlarmCondition.ConditionType conditionType;

    @NotNull(message = "조건 연산자는 필수입니다.")
    private AlarmCondition.Operator operator;

    @NotBlank(message = "조건 필드는 필수입니다.")
    private String fieldKey;

    @NotBlank(message = "조건 값은 필수입니다.")
    private String fieldValue;

    private String extraJson;
}
