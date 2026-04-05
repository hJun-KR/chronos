package kr.hjun.backend.repository;

import kr.hjun.backend.entity.AlarmExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmExecutionLogRepository extends JpaRepository<AlarmExecutionLog, Long> {

    // 알람 로그를 최신 순으로 조회한다.
    java.util.List<AlarmExecutionLog> findAllByAlarmIdOrderByExecutedAtDesc(Long alarmId);
}
