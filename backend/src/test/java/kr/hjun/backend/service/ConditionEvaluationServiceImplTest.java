package kr.hjun.backend.service;

import kr.hjun.backend.entity.AlarmCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionEvaluationServiceImplTest {

    private final ConditionEvaluationService conditionEvaluationService = new ConditionEvaluationServiceImpl();

    @Test
    @DisplayName("조건 평가 기본 성공")
    void evaluateSuccess() {
        AlarmCondition condition = AlarmCondition.builder()
                .conditionType(AlarmCondition.ConditionType.WEATHER)
                .operator(AlarmCondition.Operator.EQ)
                .fieldKey("temp")
                .fieldValue("10")
                .build();

        boolean result = conditionEvaluationService.evaluate(List.of(condition), ConditionContext.empty());

        assertThat(result).isTrue();
    }
}
