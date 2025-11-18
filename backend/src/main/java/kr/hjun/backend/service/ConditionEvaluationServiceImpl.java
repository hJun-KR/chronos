package kr.hjun.backend.service;

import kr.hjun.backend.entity.AlarmCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import kr.hjun.backend.exception.ChronosException;
import org.springframework.http.HttpStatus;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ConditionEvaluationServiceImpl implements ConditionEvaluationService {

    // 조건을 평가한다.
    @Override
    public boolean evaluate(List<AlarmCondition> conditions, ConditionContext context) {
        ConditionContext ctx = context != null ? context : ConditionContext.empty();
        for (AlarmCondition condition : conditions) {
            Object actual = switch (condition.getConditionType()) {
                case WEATHER -> ctx.weatherData().get(condition.getFieldKey());
                case STOCK -> ctx.stockData().get(condition.getFieldKey());
                case TIME_RANGE, CUSTOM -> ctx.customData().get(condition.getFieldKey());
            };
            boolean result = evaluateCondition(condition, actual);
            if (!result) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateCondition(AlarmCondition condition, Object actualValue) {
        if (actualValue == null) {
            log.info("조건 값이 제공되지 않았습니다. key={}", condition.getFieldKey());
            return false;
        }
        String expected = condition.getFieldValue();
        return switch (condition.getOperator()) {
            case EQ -> compareAsString(actualValue, expected) == 0;
            case CONTAINS -> actualValue.toString().contains(expected);
            case GT -> compareAsNumber(actualValue, expected) > 0;
            case LT -> compareAsNumber(actualValue, expected) < 0;
            case GTE -> compareAsNumber(actualValue, expected) >= 0;
            case LTE -> compareAsNumber(actualValue, expected) <= 0;
            case BETWEEN -> compareBetween(actualValue, expected);
        };
    }

    private int compareAsString(Object actual, String expected) {
        return actual.toString().compareToIgnoreCase(expected);
    }

    private int compareAsNumber(Object actual, String expected) {
        try {
            double a = Double.parseDouble(actual.toString());
            double b = Double.parseDouble(expected);
            return Double.compare(a, b);
        } catch (NumberFormatException e) {
            throw new ChronosException(HttpStatus.BAD_REQUEST, "숫자 비교가 필요한 조건입니다.");
        }
    }

    private boolean compareBetween(Object actual, String expected) {
        String[] tokens = expected.split(",");
        if (tokens.length != 2) {
            throw new ChronosException(HttpStatus.BAD_REQUEST, "BETWEEN 조건은 'min,max' 형식이어야 합니다.");
        }
        String actualStr = actual.toString();
        if (looksLikeTime(actualStr) || looksLikeTime(tokens[0]) || looksLikeTime(tokens[1])) {
            LocalTime value = parseTime(actualStr);
            LocalTime min = parseTime(tokens[0]);
            LocalTime max = parseTime(tokens[1]);
            return !value.isBefore(min) && !value.isAfter(max);
        }
        try {
            double value = Double.parseDouble(actualStr);
            double min = Double.parseDouble(tokens[0].trim());
            double max = Double.parseDouble(tokens[1].trim());
            return value >= min && value <= max;
        } catch (NumberFormatException e) {
            throw new ChronosException(HttpStatus.BAD_REQUEST, "BETWEEN 조건 값이 올바르지 않습니다.");
        }
    }

    private boolean looksLikeTime(String value) {
        return value.contains(":");
    }

    private LocalTime parseTime(String value) {
        String trimmed = value.trim();
        if (!trimmed.contains(":")) {
            return LocalTime.of(Integer.parseInt(trimmed), 0);
        }
        return LocalTime.parse(trimmed.length() == 5 ? trimmed : trimmed.substring(0, 5));
    }
}
