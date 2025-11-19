package kr.hjun.backend.dto;

import kr.hjun.backend.service.ConditionContext;

import java.util.Map;

public record AlarmSimulationRequest(
        Map<String, Object> weatherData,
        Map<String, Object> stockData,
        Map<String, Object> customData,
        String timezone,
        boolean sendNotification
) {

    public ConditionContext toContext(String defaultTimezone) {
        String zone = timezone != null && !timezone.isBlank() ? timezone : defaultTimezone;
        return new ConditionContext(
                weatherData != null ? weatherData : Map.of(),
                stockData != null ? stockData : Map.of(),
                customData != null ? customData : Map.of(),
                zone != null ? zone : "Asia/Seoul"
        );
    }
}
