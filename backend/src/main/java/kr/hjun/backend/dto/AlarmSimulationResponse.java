package kr.hjun.backend.dto;

public record AlarmSimulationResponse(
        boolean conditionsMet,
        boolean notificationSent,
        String message
) {
}
