package kr.hjun.backend.service;

import kr.hjun.backend.dto.AlarmConditionRequest;
import kr.hjun.backend.dto.AlarmCreateRequest;
import kr.hjun.backend.dto.AlarmResponse;
import kr.hjun.backend.dto.AlarmUpdateRequest;
import kr.hjun.backend.entity.Alarm;
import kr.hjun.backend.entity.User;
import kr.hjun.backend.exception.ChronosException;
import kr.hjun.backend.repository.AlarmRepository;
import kr.hjun.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create")
@Import({AlarmServiceImpl.class, AlarmServiceImplTest.TestConfig.class})
class AlarmServiceImplTest {

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    // 알람을 성공적으로 생성한다.
    @Test
    @DisplayName("알람 생성 성공")
    void createAlarm() {
        User user = createUser("alarm@example.com");

        AlarmCreateRequest request = createRequest();

        AlarmResponse response = alarmService.createAlarm(user.getId(), request);

        assertThat(response.name()).isEqualTo("테스트 알람");
        assertThat(response.conditions()).hasSize(1);
    }

    // 다른 사용자가 알람을 조회하면 실패한다.
    @Test
    @DisplayName("알람 조회 실패 - 권한 없음")
    void getAlarm_notOwner() {
        User owner = createUser("owner@example.com");
        User other = createUser("other@example.com");

        AlarmResponse created = alarmService.createAlarm(owner.getId(), createRequest());

        assertThatThrownBy(() -> alarmService.getAlarm(other.getId(), created.id()))
                .isInstanceOf(ChronosException.class);
    }

    // 알람을 수정하면 조건이 갱신된다.
    @Test
    @DisplayName("알람 수정 성공")
    void updateAlarm() {
        User owner = createUser("owner2@example.com");
        AlarmResponse created = alarmService.createAlarm(owner.getId(), createRequest());

        AlarmUpdateRequest updateRequest = new AlarmUpdateRequest();
        updateRequest.setName("변경된 알람");
        updateRequest.setDescription("설명");
        updateRequest.setAlarmType(Alarm.AlarmType.CUSTOM);
        updateRequest.setScheduleType(Alarm.ScheduleType.RECURRING);
        updateRequest.setCronExpression("0 0 * * *");
        updateRequest.setRunAt(null);
        updateRequest.setTimezone("UTC");
        updateRequest.setChannel(Alarm.AlarmChannel.EMAIL);
        updateRequest.setTargetAddress("user@example.com");
        updateRequest.setStatus(Alarm.AlarmStatus.PAUSED);
        AlarmConditionRequest conditionRequest = new AlarmConditionRequest();
        conditionRequest.setConditionType(kr.hjun.backend.entity.AlarmCondition.ConditionType.CUSTOM);
        conditionRequest.setOperator(kr.hjun.backend.entity.AlarmCondition.Operator.CONTAINS);
        conditionRequest.setFieldKey("payload");
        conditionRequest.setFieldValue("hello");
        updateRequest.setConditions(List.of(conditionRequest));

        AlarmResponse updated = alarmService.updateAlarm(owner.getId(), created.id(), updateRequest);

        assertThat(updated.name()).isEqualTo("변경된 알람");
        assertThat(updated.conditions()).hasSize(1);
        assertThat(updated.status()).isEqualTo(Alarm.AlarmStatus.PAUSED);
    }

    private AlarmCreateRequest createRequest() {
        AlarmCreateRequest request = new AlarmCreateRequest();
        request.setName("테스트 알람");
        request.setDescription("매일 알림");
        request.setAlarmType(Alarm.AlarmType.TIME);
        request.setScheduleType(Alarm.ScheduleType.ONCE);
        request.setRunAt(LocalDateTime.now().plusDays(1));
        request.setTimezone("Asia/Seoul");
        request.setChannel(Alarm.AlarmChannel.EMAIL);
        request.setTargetAddress("alarm@example.com");
        AlarmConditionRequest conditionRequest = new AlarmConditionRequest();
        conditionRequest.setConditionType(kr.hjun.backend.entity.AlarmCondition.ConditionType.TIME_RANGE);
        conditionRequest.setOperator(kr.hjun.backend.entity.AlarmCondition.Operator.EQ);
        conditionRequest.setFieldKey("hour");
        conditionRequest.setFieldValue("7");
        request.setConditions(List.of(conditionRequest));
        return request;
    }

    private User createUser(String email) {
        User user = User.builder()
                .email(email)
                .password("encoded")
                .name("사용자")
                .role(User.Role.USER)
                .isActive(true)
                .build();
        return userRepository.save(user);
    }
}
