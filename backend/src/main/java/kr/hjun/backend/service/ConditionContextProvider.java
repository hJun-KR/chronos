package kr.hjun.backend.service;

import kr.hjun.backend.entity.Alarm;

public interface ConditionContextProvider {

    ConditionContext buildContext(Alarm alarm);
}
