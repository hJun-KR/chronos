package kr.hjun.backend.dto;

public record AuthResponse(
        UserResponse user,
        TokenResponse token
) {
}
