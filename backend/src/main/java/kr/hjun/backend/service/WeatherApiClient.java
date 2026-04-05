package kr.hjun.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherApiClient {

    private final RestTemplate restTemplate;

    @Value("${WEATHER_API_URL:http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst}")
    private String weatherApiUrl;

    @Value("${WEATHER_API_KEY:}")
    private String serviceKey;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH00");

    // 외부 날씨 데이터를 조회한다.
    public Map<String, Object> fetch(Map<String, Object> params) {
        if (serviceKey == null || serviceKey.isBlank()) {
            return Collections.emptyMap();
        }

        Map<String, Object> query = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        query.put("base_date", params.getOrDefault("base_date", DATE_FMT.format(now)));
        query.put("base_time", params.getOrDefault("base_time", TIME_FMT.format(now.minusHours(1))));
        query.put("nx", params.getOrDefault("nx", "60"));
        query.put("ny", params.getOrDefault("ny", "127"));
        query.put("pageNo", params.getOrDefault("pageNo", "1"));
        query.put("numOfRows", params.getOrDefault("numOfRows", "100"));
        query.put("dataType", "JSON");

        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(weatherApiUrl)
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("pageNo", query.get("pageNo"))
                    .queryParam("numOfRows", query.get("numOfRows"))
                    .queryParam("dataType", "JSON")
                    .queryParam("base_date", query.get("base_date"))
                    .queryParam("base_time", query.get("base_time"))
                    .queryParam("nx", query.get("nx"))
                    .queryParam("ny", query.get("ny"))
                    .build(true)
                    .toUri();
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return extractCategoryValues(response.getBody());
        } catch (Exception e) {
            log.warn("날씨 API 호출 실패", e);
            return Collections.emptyMap();
        }
    }

    private Map<String, Object> extractCategoryValues(Map body) {
        if (body == null) {
            return Collections.emptyMap();
        }
        Object responseObj = body.get("response");
        if (!(responseObj instanceof Map responseMap)) {
            return Collections.emptyMap();
        }
        Object bodyObj = responseMap.get("body");
        if (!(bodyObj instanceof Map bodyMap)) {
            return Collections.emptyMap();
        }
        Object itemsObj = bodyMap.get("items");
        if (!(itemsObj instanceof Map itemsMap)) {
            return Collections.emptyMap();
        }
        Object itemList = itemsMap.get("item");
        if (!(itemList instanceof List<?> list)) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = new HashMap<>();
        for (Object itemObj : list) {
            if (itemObj instanceof Map item) {
                Object category = item.get("category");
                Object value = item.get("obsrValue");
                if (category != null && value != null) {
                    result.put(category.toString(), value);
                }
            }
        }
        return result;
    }
}
