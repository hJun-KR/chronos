package kr.hjun.backend.exception;

import org.springframework.http.HttpStatus;

public class ChronosException extends RuntimeException {

    private final HttpStatus status;

    // 예외를 초기화한다.
    public ChronosException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    // HTTP 상태를 반환한다.
    public HttpStatus getStatus() {
        return status;
    }
}
