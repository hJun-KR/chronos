package kr.hjun.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.hjun.backend.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 값입니다.")
    private String email;

    @Size(min = 8, max = 64, message = "비밀번호는 8~64자여야 합니다.")
    @NotBlank(message = "비밀번호는 필수 값입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 값입니다.")
    private String name;

    // DTO를 엔티티로 변환한다.
    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(this.email)
                .password(encodedPassword)
                .name(this.name)
                .role(User.Role.USER)
                .isActive(false)
                .build();
    }
}
