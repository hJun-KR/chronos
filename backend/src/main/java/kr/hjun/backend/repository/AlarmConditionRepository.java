package kr.hjun.backend.repository;

import kr.hjun.backend.entity.AlarmCondition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmConditionRepository extends JpaRepository<AlarmCondition, Long> {
}
