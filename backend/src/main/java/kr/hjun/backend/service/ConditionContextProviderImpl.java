package kr.hjun.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hjun.backend.entity.Alarm;
import kr.hjun.backend.entity.AlarmCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConditionContextProviderImpl implements ConditionContextProvider {

    private static final String SEOUL_NX = "60";
    private static final String SEOUL_NY = "127";

    private final WeatherApiClient weatherApiClient;
    private final StockApiClient stockApiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ConditionContext buildContext(Alarm alarm) {
        List<AlarmCondition> conditions = alarm.getConditions();
        Map<String, Object> weatherData = new HashMap<>();
        Map<String, Object> stockData = new HashMap<>();
        Map<String, Object> customData = new HashMap<>();

        Map<String, Object> weatherParams = new HashMap<>();
        Map<String, Object> stockParams = new HashMap<>();

        for (AlarmCondition condition : conditions) {
            Map<String, Object> extras = parseExtras(condition.getExtraJson());
            switch (condition.getConditionType()) {
                case WEATHER -> {
                    weatherParams.putAll(extras);
                    weatherParams.put("nx", SEOUL_NX);
                    weatherParams.put("ny", SEOUL_NY);
                    if (extras.containsKey("value")) {
                        weatherData.put(condition.getFieldKey(), extras.get("value"));
                    }
                }
                case STOCK -> {
                    String symbol = (String) extras.get("symbol");
                    String market = (String) extras.get("market");
                    if (symbol != null) {
                        Map<String, Object> query = new HashMap<>(extras);
                        Map<String, Object> quote = stockApiClient.fetch(query);
                        // 조회된 가격 정보를 필드 키와 연계하여 데이터 컨텍스트에 저장
                        if (quote != null && !quote.isEmpty()) {
                            stockData.putAll(quote);
                        }
                    }
                    if (extras.containsKey("value")) {
                        stockData.put(condition.getFieldKey(), extras.get("value"));
                    }
                }
                case TIME_RANGE, CUSTOM -> {
                    if (extras.containsKey("value")) {
                        customData.put(condition.getFieldKey(), extras.get("value"));
                    }
                }
            }
        }

        if (!weatherParams.isEmpty()) {
            Map<String, Object> remote = weatherApiClient.fetch(weatherParams);
            weatherData.putAll(remote);
        }

        String timezone = alarm.getTimezone() != null ? alarm.getTimezone() : "Asia/Seoul";
        return new ConditionContext(weatherData, stockData, customData, timezone);
    }

    private Map<String, Object> parseExtras(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            log.warn("extraJson 파싱 실패: {}", json);
            return Map.of();
        }
    }
}
