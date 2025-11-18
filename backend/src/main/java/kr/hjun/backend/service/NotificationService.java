package kr.hjun.backend.service;

import kr.hjun.backend.entity.Alarm;

public interface NotificationService {

    // 알림을 발송한다.
    void send(Alarm alarm, String payload);
}
