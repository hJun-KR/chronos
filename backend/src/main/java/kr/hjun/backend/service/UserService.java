package kr.hjun.backend.service;

import kr.hjun.backend.dto.LoginRequest;
import kr.hjun.backend.dto.UserCreateRequest;
import kr.hjun.backend.dto.UserResponse;

public interface UserService {

    // 새 사용자를 등록한다.
    UserResponse register(UserCreateRequest request);

    // 기존 사용자를 로그인시킨다.
    UserResponse login(LoginRequest request);
}
