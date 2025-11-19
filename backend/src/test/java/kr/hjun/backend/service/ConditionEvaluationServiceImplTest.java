package kr.hjun.backend.service;

import kr.hjun.backend.entity.AlarmCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionEvaluationServiceImplTest {

    private final ConditionEvaluationService conditionEvaluationService = new ConditionEvaluationServiceImpl();

    @Test
    @DisplayName("숫자 EQ 조건 평가")
    void evaluateSuccess() {
        AlarmCondition condition = AlarmCondition.builder()
                .conditionType(AlarmCondition.ConditionType.WEATHER)
                .operator(AlarmCondition.Operator.EQ)
                .fieldKey("temp")
                .fieldValue("10")
                .build();

        ConditionContext context = new ConditionContext(Map.of("temp", "10"), Map.of(), Map.of(), "Asia/Seoul");
        boolean result = conditionEvaluationService.evaluate(List.of(condition), context);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("조건 평가 - BETWEEN 시간대")
    void evaluateBetweenTime() {
        AlarmCondition condition = AlarmCondition.builder()
                .conditionType(AlarmCondition.ConditionType.CUSTOM)
                .operator(AlarmCondition.Operator.BETWEEN)
                .fieldKey("time")
                .fieldValue("09:00,12:00")
                .build();
        ConditionContext context = new ConditionContext(Map.of(), Map.of(), Map.of("time", "10:30"), "Asia/Seoul");
        boolean result = conditionEvaluationService.evaluate(List.of(condition), context);
        assertThat(result).isTrue();
    }
}
