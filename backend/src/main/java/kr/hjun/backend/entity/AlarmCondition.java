package kr.hjun.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alarm_conditions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmCondition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_condition_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id", nullable = false)
    private Alarm alarm;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false, length = 20)
    private ConditionType conditionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator", nullable = false, length = 10)
    private Operator operator;

    @Column(name = "field_key", nullable = false, length = 50)
    private String fieldKey;

    @Column(name = "field_value", nullable = false, length = 200)
    private String fieldValue;

    @Column(name = "extra_json", columnDefinition = "TEXT")
    private String extraJson;

    public enum ConditionType {
        WEATHER, STOCK, TIME_RANGE, CUSTOM
    }

    public enum Operator {
        EQ, GT, LT, GTE, LTE, BETWEEN, CONTAINS
    }
}
