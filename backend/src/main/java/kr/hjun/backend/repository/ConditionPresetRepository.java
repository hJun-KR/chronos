package kr.hjun.backend.repository;

import kr.hjun.backend.entity.ConditionPreset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConditionPresetRepository extends JpaRepository<ConditionPreset, Long> {
    List<ConditionPreset> findAllByUserId(Long userId);
}
