package kr.hjun.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherApiClient {

    private final RestTemplate restTemplate;

    @Value("${WEATHER_API_URL:}")
    private String weatherApiUrl;

    // 외부 날씨 데이터를 조회한다.
    public Map<String, Object> fetch(Map<String, Object> params) {
        if (weatherApiUrl == null || weatherApiUrl.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(weatherApiUrl, Map.class, params);
            return response.getBody() != null ? response.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.warn("날씨 API 호출 실패", e);
            return Collections.emptyMap();
        }
    }
}
