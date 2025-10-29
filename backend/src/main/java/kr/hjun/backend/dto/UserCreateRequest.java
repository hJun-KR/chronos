package kr.hjun.backend.dto;

import kr.hjun.backend.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {
    private String email;
    private String password;
    private String name;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(this.email)
                .password(encodedPassword)
                .name(this.name)
                .role(User.Role.USER)
                .isActive(true)
                .build();
    }
}
