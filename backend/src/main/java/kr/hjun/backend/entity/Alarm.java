package kr.hjun.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alarms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_type", nullable = false, length = 20)
    private AlarmType alarmType;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false, length = 20)
    private ScheduleType scheduleType;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "run_at")
    private LocalDateTime runAt;

    @Column(name = "timezone", length = 40)
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private AlarmChannel channel;

    @Column(name = "target_address", nullable = false, length = 200)
    private String targetAddress;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private AlarmStatus status = AlarmStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_type", length = 20)
    private RecurrenceType recurrenceType;

    @Column(name = "recurrence_days", length = 100)
    private String recurrenceDays;

    @Column(name = "recurrence_day_of_month")
    private Integer recurrenceDayOfMonth;

    @Column(name = "recurrence_month_of_year")
    private Integer recurrenceMonthOfYear;

    @Column(name = "last_run_at")
    private LocalDateTime lastRunAt;

    @Column(name = "next_run_at")
    private LocalDateTime nextRunAt;

    @Column(name = "last_result", length = 50)
    private String lastResult;

    @Builder.Default
    @OneToMany(mappedBy = "alarm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlarmCondition> conditions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "alarm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlarmExecutionLog> executionLogs = new ArrayList<>();

    // 조건을 모두 제거한다.
    public void clearConditions() {
        this.conditions.clear();
    }

    // 조건을 추가한다.
    public void addCondition(AlarmCondition condition) {
        condition.setAlarm(this);
        this.conditions.add(condition);
    }

    public enum AlarmType {
        TIME, WEATHER, STOCK, CUSTOM
    }

    public enum ScheduleType {
        ONCE, RECURRING
    }

    public enum AlarmChannel {
        EMAIL, DISCORD
    }

    public enum AlarmStatus {
        ACTIVE, PAUSED
    }

    public enum RecurrenceType {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }
}
