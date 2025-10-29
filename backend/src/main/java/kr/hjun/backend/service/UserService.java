package kr.hjun.backend.service;

import kr.hjun.backend.dto.LoginRequest;
import kr.hjun.backend.dto.UserCreateRequest;
import kr.hjun.backend.dto.UserResponse;
import kr.hjun.backend.entity.User;
import kr.hjun.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse register(UserCreateRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        // 탈퇴 계정 확인
        if (userRepository.existsByEmailAndIsActiveFalse(request.getEmail())) {
            throw new RuntimeException("탈되된 계정입니다");
        }

        User newUser = request.toEntity(request.getPassword());
        User savedUser = userRepository.save(newUser);

        return new UserResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                .orElseThrow(() -> new RuntimeException("가입되지 않은 이메일이거나 비활성 계정입니다."));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return new UserResponse(user);
    }
}