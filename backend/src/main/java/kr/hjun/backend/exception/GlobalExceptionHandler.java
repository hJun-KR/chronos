package kr.hjun.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Chronos 예외를 처리한다.
    @ExceptionHandler(ChronosException.class)
    public ResponseEntity<ErrorResponse> handleChronosException(ChronosException exception) {
        ErrorResponse response = ErrorResponse.of(exception.getStatus(), exception.getMessage(), List.of());
        return ResponseEntity.status(exception.getStatus()).body(response);
    }

    // 검증 실패 예외를 처리한다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());
        ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST, "유효성 검사에 실패했습니다.", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 알 수 없는 예외를 처리한다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception exception) {
        ErrorResponse response = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.", List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 필드 오류를 문자열로 변환한다.
    private String formatFieldError(FieldError error) {
        return "%s: %s".formatted(error.getField(), error.getDefaultMessage());
    }
}
