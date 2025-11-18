package kr.hjun.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.hjun.backend.entity.Alarm;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AlarmUpdateRequest {

    @NotBlank(message = "알람 이름은 필수입니다.")
    private String name;

    private String description;

    @NotNull(message = "알람 유형은 필수입니다.")
    private Alarm.AlarmType alarmType;

    @NotNull(message = "스케줄 유형은 필수입니다.")
    private Alarm.ScheduleType scheduleType;

    private String cronExpression;

    private LocalDateTime runAt;

    @NotBlank(message = "시간대는 필수입니다.")
    private String timezone;

    @NotNull(message = "채널은 필수입니다.")
    private Alarm.AlarmChannel channel;

    @NotBlank(message = "대상 주소는 필수입니다.")
    private String targetAddress;

    @Valid
    @NotEmpty(message = "최소 한 개의 조건이 필요합니다.")
    private List<AlarmConditionRequest> conditions = new ArrayList<>();

    @NotNull(message = "상태는 필수입니다.")
    private Alarm.AlarmStatus status;
}
