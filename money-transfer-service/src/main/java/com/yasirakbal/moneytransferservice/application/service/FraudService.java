package com.yasirakbal.moneytransferservice.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudService {

    private final WebClient.Builder webClientBuilder;
    private final RedisTemplate<String, Boolean> redisTemplate;

    public boolean isFraudulent(UUID accountId, BigDecimal amount) {
        String cacheKey = "fraud:" + accountId + ":" + amount.stripTrailingZeros().toPlainString();

        Boolean cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("Fraud check cache hit. accountId={}", accountId);
            return cached;
        }

        FraudCheckResponse response = webClientBuilder.build()
                .post()
                .uri("http://fraud-service:8083/api/v1/fraud/check")
                .bodyValue(new FraudCheckRequest(accountId, amount))
                .retrieve()
                .bodyToMono(FraudCheckResponse.class)
                .block();

        boolean result = response != null && response.fraudulent();

        redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(5));

        return result;
    }

    record FraudCheckRequest(UUID accountId, BigDecimal amount) {}
    record FraudCheckResponse(boolean fraudulent, String reason) {}
}