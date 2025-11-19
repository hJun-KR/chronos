package kr.hjun.backend.service;

import kr.hjun.backend.entity.Alarm;

public interface AlarmSchedulingService {

    // 알람의 다음 실행 시간을 계산한다.
    void updateNextRun(Alarm alarm);
}
