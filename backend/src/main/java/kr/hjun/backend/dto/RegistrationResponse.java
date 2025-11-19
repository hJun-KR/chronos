package kr.hjun.backend.dto;

public record RegistrationResponse(
        UserResponse user,
        String message
) {
}
