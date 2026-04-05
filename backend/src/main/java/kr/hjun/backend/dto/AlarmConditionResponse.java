package kr.hjun.backend.dto;

import kr.hjun.backend.entity.AlarmCondition;

public record AlarmConditionResponse(
        Long id,
        AlarmCondition.ConditionType conditionType,
        AlarmCondition.Operator operator,
        String fieldKey,
        String fieldValue,
        String extraJson
) {

    public static AlarmConditionResponse from(AlarmCondition condition) {
        return new AlarmConditionResponse(
                condition.getId(),
                condition.getConditionType(),
                condition.getOperator(),
                condition.getFieldKey(),
                condition.getFieldValue(),
                condition.getExtraJson()
        );
    }
}
