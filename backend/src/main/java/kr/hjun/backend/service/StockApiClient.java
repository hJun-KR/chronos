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
        String symbol = (String) params.get("symbol");
        String market = (String) params.get("market");

        if (symbol == null || symbol.isBlank()) {
            return Collections.emptyMap();
        }

        log.info("Fetching stock data for Symbol: {}, Market: {}", symbol, market);

        // 실서비스에서는 여기서 외부 API를 호출합니다.
        // 현재는 데모를 위해 가상 데이터를 생성합니다.
        double basePrice = market != null && market.contains("NASDAQ") ? 150.0 : 70000.0;
        double randomChange = (Math.random() - 0.5) * 2.0; // -1% ~ +1% 변동
        double currentPrice = basePrice * (1 + randomChange / 100);

        return Map.of(
            "price", Math.round(currentPrice * 100.0) / 100.0,
            "change_rate", Math.round(randomChange * 100.0) / 100.0,
            "symbol", symbol,
            "market", market != null ? market : "UNKNOWN"
        );
    }
}
