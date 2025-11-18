package kr.hjun.backend.service;

import kr.hjun.backend.entity.AlarmCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ConditionEvaluationServiceImpl implements ConditionEvaluationService {

    // 조건을 평가한다.
    @Override
    public boolean evaluate(List<AlarmCondition> conditions, ConditionContext context) {
        for (AlarmCondition condition : conditions) {
            boolean result = switch (condition.getConditionType()) {
                case WEATHER -> evaluateWeather(condition, context);
                case STOCK -> evaluateStock(condition, context);
                case TIME_RANGE -> evaluateTimeRange(condition, context);
                case CUSTOM -> evaluateCustom(condition, context);
            };
            if (!result) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateWeather(AlarmCondition condition, ConditionContext context) {
        log.debug("WEATHER 조건 평가 - key: {}, value: {}", condition.getFieldKey(), condition.getFieldValue());
        return true;
    }

    private boolean evaluateStock(AlarmCondition condition, ConditionContext context) {
        log.debug("STOCK 조건 평가 - key: {}, value: {}", condition.getFieldKey(), condition.getFieldValue());
        return true;
    }

    private boolean evaluateTimeRange(AlarmCondition condition, ConditionContext context) {
        log.debug("TIME_RANGE 조건 평가 - key: {}, value: {}", condition.getFieldKey(), condition.getFieldValue());
        return true;
    }

    private boolean evaluateCustom(AlarmCondition condition, ConditionContext context) {
        log.debug("CUSTOM 조건 평가 - key: {}, value: {}", condition.getFieldKey(), condition.getFieldValue());
        return true;
    }
}
