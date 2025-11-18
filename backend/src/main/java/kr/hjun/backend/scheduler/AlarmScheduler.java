package kr.hjun.backend.scheduler;

import kr.hjun.backend.entity.Alarm;
import kr.hjun.backend.repository.AlarmRepository;
import kr.hjun.backend.service.AlarmExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmScheduler {

    private final TaskScheduler taskScheduler;
    private final AlarmRepository alarmRepository;
    private final AlarmExecutionService alarmExecutionService;
    private final Map<Long, Runnable> scheduledTasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        alarmRepository.findAll().forEach(this::schedule);
    }

    // 알람을 스케줄링한다.
    public void schedule(Alarm alarm) {
        if (alarm.getNextRunAt() == null) {
            return;
        }
        cancel(alarm.getId());
        Runnable task = () -> {
            alarmExecutionService.execute(alarm);
            alarmRepository.findById(alarm.getId()).ifPresent(this::schedule);
        };
        scheduledTasks.put(alarm.getId(), task);
        taskScheduler.schedule(task, Instant.from(alarm.getNextRunAt().atZone(ZoneId.systemDefault())));
        log.info("알람 스케줄링: {} - {}", alarm.getId(), alarm.getNextRunAt());
    }

    // 스케줄을 취소한다.
    public void cancel(Long alarmId) {
        scheduledTasks.remove(alarmId);
    }
}
