package kr.hjun.backend.service;

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
public class StockApiClient {

    private final RestTemplate restTemplate;

    @Value("${STOCK_API_URL:}")
    private String stockApiUrl;

    // 주가 데이터를 조회한다.
    public Map<String, Object> fetch(Map<String, Object> params) {
        if (stockApiUrl == null || stockApiUrl.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(stockApiUrl, Map.class, params);
            return response.getBody() != null ? response.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.warn("주가 API 호출 실패", e);
            return Collections.emptyMap();
        }
    }
}
