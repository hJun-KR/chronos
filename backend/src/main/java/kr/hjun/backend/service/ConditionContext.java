package kr.hjun.backend.service;

import java.util.Map;

public record ConditionContext(
        Map<String, Object> weatherData,
        Map<String, Object> stockData,
        Map<String, Object> customData
) {

    public static ConditionContext empty() {
        return new ConditionContext(Map.of(), Map.of(), Map.of());
    }
}
