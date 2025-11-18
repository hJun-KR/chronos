package kr.hjun.backend.repository;

import kr.hjun.backend.entity.AlarmExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmExecutionLogRepository extends JpaRepository<AlarmExecutionLog, Long> {
}
