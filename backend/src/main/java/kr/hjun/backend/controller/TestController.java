package kr.hjun.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    // 서버 헬스 체크를 응답한다.
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
