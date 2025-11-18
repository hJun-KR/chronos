package kr.hjun.backend.dto;

import kr.hjun.backend.service.ConditionContext;

import java.util.Map;

public record AlarmSimulationRequest(
        Map<String, Object> weatherData,
        Map<String, Object> stockData,
        Map<String, Object> customData,
        boolean sendNotification
) {

    public ConditionContext toContext() {
        return new ConditionContext(
                weatherData != null ? weatherData : Map.of(),
                stockData != null ? stockData : Map.of(),
                customData != null ? customData : Map.of()
        );
    }
}
