package kr.hjun.backend.service;

import jakarta.annotation.PostConstruct;
import kr.hjun.backend.dto.AlarmConditionRequest;
import kr.hjun.backend.dto.ConditionPresetResponse;
import kr.hjun.backend.entity.AlarmCondition;
import kr.hjun.backend.exception.ChronosException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConditionPresetServiceImpl implements ConditionPresetService {

    private final List<ConditionPresetResponse> presets = new ArrayList<>();

    @PostConstruct
    void init() {
        presets.add(buildWeatherPreset());
        presets.add(buildStockPreset());
        presets.add(buildTimePreset());
    }

    @Override
    public List<ConditionPresetResponse> getPresets() {
        return presets;
    }

    @Override
    public ConditionPresetResponse getPreset(String key) {
        return presets.stream()
                .filter(p -> p.key().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new ChronosException(HttpStatus.NOT_FOUND, "프리셋을 찾을 수 없습니다."));
    }

    private ConditionPresetResponse buildWeatherPreset() {
        AlarmConditionRequest condition = new AlarmConditionRequest();
        condition.setConditionType(AlarmCondition.ConditionType.WEATHER);
        condition.setOperator(AlarmCondition.Operator.LT);
        condition.setFieldKey("T1H");
        condition.setFieldValue("5");
        condition.setExtraJson(Map.of(
                "base_time", "0600"
        ));
        return new ConditionPresetResponse(
                "seoul-morning-cold",
                "서울 아침 기온 5℃ 이하",
                "기상청 초단기 실황에서 현재 기온(T1H)이 5도 미만일 때 실행",
                List.of(condition)
        );
    }

    private ConditionPresetResponse buildStockPreset() {
        AlarmConditionRequest condition = new AlarmConditionRequest();
        condition.setConditionType(AlarmCondition.ConditionType.STOCK);
        condition.setOperator(AlarmCondition.Operator.LTE);
        condition.setFieldKey("price");
        condition.setFieldValue("65000");
        condition.setExtraJson(Map.of(
                "symbol", "005930.KS"
        ));
        return new ConditionPresetResponse(
                "kospi-samsung-buy",
                "삼성전자 6만5천원 이하",
                "주가 데이터가 65,000원 이하인 경우",
                List.of(condition)
        );
    }

    private ConditionPresetResponse buildTimePreset() {
        AlarmConditionRequest condition = new AlarmConditionRequest();
        condition.setConditionType(AlarmCondition.ConditionType.TIME_RANGE);
        condition.setOperator(AlarmCondition.Operator.BETWEEN);
        condition.setFieldKey("hour");
        condition.setFieldValue("09:00,18:00");
        condition.setExtraJson(Map.of(
                "value", "10:00"
        ));
        return new ConditionPresetResponse(
                "working-hours",
                "업무 시간대 9시~18시",
                "현재 시간이 9~18시 사이일 때 실행",
                List.of(condition)
        );
    }
}
