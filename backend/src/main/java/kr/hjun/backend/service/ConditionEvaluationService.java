package kr.hjun.backend.service;

import kr.hjun.backend.entity.AlarmCondition;

import java.util.List;

public interface ConditionEvaluationService {

    // 조건을 평가한다.
    boolean evaluate(List<AlarmCondition> conditions, ConditionContext context);
}
