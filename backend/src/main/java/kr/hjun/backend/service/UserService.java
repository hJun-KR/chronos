package kr.hjun.backend.service;

import kr.hjun.backend.dto.LoginRequest;
import kr.hjun.backend.dto.PasswordChangeRequest;
import kr.hjun.backend.dto.UserCreateRequest;
import kr.hjun.backend.entity.User;

public interface UserService {

    // 새 사용자를 등록한다.
    User register(UserCreateRequest request);

    // 기존 사용자를 로그인시킨다.
    User login(LoginRequest request);

    // 비밀번호를 변경한다.
    void changePassword(Long userId, PasswordChangeRequest request);
}
