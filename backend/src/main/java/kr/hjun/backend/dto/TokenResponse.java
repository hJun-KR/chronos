package kr.hjun.backend.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {

    // 토큰 정보를 생성한다.
    public static TokenResponse of(String accessToken, String refreshToken, long expiresIn) {
        return new TokenResponse(accessToken, refreshToken, expiresIn);
    }
}
