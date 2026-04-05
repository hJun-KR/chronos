package kr.hjun.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationConfirmRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Size(min = 6, max = 6, message = "인증 코드는 6자리여야 합니다.")
    @NotBlank(message = "인증 코드는 필수입니다.")
    private String code;
}
