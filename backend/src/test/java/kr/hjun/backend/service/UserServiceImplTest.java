package kr.hjun.backend.service;

import kr.hjun.backend.dto.LoginRequest;
import kr.hjun.backend.dto.UserCreateRequest;
import kr.hjun.backend.dto.UserResponse;
import kr.hjun.backend.entity.User;
import kr.hjun.backend.exception.ChronosException;
import kr.hjun.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create")
@Import({UserServiceImpl.class, UserServiceImplTest.PasswordEncoderTestConfig.class})
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @TestConfiguration
    static class PasswordEncoderTestConfig {

        // 테스트용 패스워드 인코더를 구성한다.
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    // 새로운 사용자를 성공적으로 등록한다.
    @Test
    @DisplayName("회원 가입 성공")
    void register_success() {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setName("테스터");

        UserResponse response = userService.register(request);

        assertThat(response.getId()).isNotNull();

        User savedUser = userRepository.findByEmail("test@example.com").orElseThrow();
        assertThat(savedUser.getPassword()).isNotEqualTo("password123");
    }

    // 중복 이메일 등록 시 예외가 발생한다.
    @Test
    @DisplayName("회원 가입 실패 - 중복 이메일")
    void register_duplicateEmail() {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("dup@example.com");
        request.setPassword("password123");
        request.setName("사용자1");

        userService.register(request);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(ChronosException.class);
    }

    // 정상적으로 로그인한다.
    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("login@example.com");
        request.setPassword("password123");
        request.setName("사용자");
        userService.register(request);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("login@example.com");
        loginRequest.setPassword("password123");

        UserResponse response = userService.login(loginRequest);

        assertThat(response.getEmail()).isEqualTo("login@example.com");
    }

    // 비밀번호가 다르면 예외를 던진다.
    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_wrongPassword() {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("wrongpw@example.com");
        request.setPassword("password123");
        request.setName("사용자");
        userService.register(request);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("wrongpw@example.com");
        loginRequest.setPassword("invalid123");

        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(ChronosException.class);
    }
}
