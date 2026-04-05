package kr.hjun.backend.service;

import kr.hjun.backend.dto.LoginRequest;
import kr.hjun.backend.dto.PasswordChangeRequest;
import kr.hjun.backend.dto.UserCreateRequest;
import kr.hjun.backend.entity.User;
import kr.hjun.backend.exception.ChronosException;
import kr.hjun.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 새 사용자를 등록한다.
    @Override
    @Transactional
    public User register(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ChronosException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        if (userRepository.existsByEmailAndIsActiveFalse(request.getEmail())) {
            throw new ChronosException(HttpStatus.FORBIDDEN, "탈퇴된 계정입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = request.toEntity(encodedPassword);
        return userRepository.save(newUser);
    }

    // 기존 사용자를 로그인시킨다.
    @Override
    public User login(LoginRequest request) {
        User user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                .orElseThrow(() -> new ChronosException(HttpStatus.NOT_FOUND, "가입되지 않은 이메일이거나 비활성 계정입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ChronosException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    // 비밀번호를 변경한다.
    @Override
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ChronosException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ChronosException(HttpStatus.UNAUTHORIZED, "현재 비밀번호가 올바르지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }
}
