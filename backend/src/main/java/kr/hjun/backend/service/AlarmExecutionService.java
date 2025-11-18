package kr.hjun.backend.service;

import kr.hjun.backend.dto.AlarmSimulationResponse;
import kr.hjun.backend.entity.Alarm;

public interface AlarmExecutionService {

    // 알람을 실행한다.
    void execute(Alarm alarm);

    // 사용자 입력 컨텍스트로 시뮬레이션한다.
    AlarmSimulationResponse simulate(Alarm alarm, ConditionContext context, boolean sendNotification);
}
