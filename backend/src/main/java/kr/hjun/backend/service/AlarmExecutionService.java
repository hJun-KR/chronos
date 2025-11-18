package kr.hjun.backend.service;

import kr.hjun.backend.entity.Alarm;

public interface AlarmExecutionService {

    // 알람을 실행한다.
    void execute(Alarm alarm);
}
