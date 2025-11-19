package kr.hjun.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String oldPassword;

    @Size(min = 8, max = 64, message = "새 비밀번호는 8~64자여야 합니다.")
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;
}
