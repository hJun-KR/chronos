package kr.hjun.backend.repository;

import kr.hjun.backend.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    // 사용자 소유 알람을 조회한다.
    Optional<Alarm> findByIdAndUserId(Long id, Long userId);

    // 사용자의 알람 목록을 조회한다.
    List<Alarm> findAllByUserId(Long userId);
}
