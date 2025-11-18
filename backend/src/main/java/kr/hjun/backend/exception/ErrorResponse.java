package kr.hjun.backend.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        List<String> errors
) {

    // 오류 응답을 생성한다.
    public static ErrorResponse of(HttpStatus status, String message, List<String> errors) {
        return new ErrorResponse(status.value(), message, LocalDateTime.now(), errors);
    }
}
