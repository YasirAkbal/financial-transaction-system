package com.yasirakbal.fraudservice.service;

import com.yasirakbal.fraudservice.dto.FraudCheckRequest;
import com.yasirakbal.fraudservice.dto.FraudCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private static final BigDecimal MAX_SINGLE_TRANSFER = new BigDecimal("100000");

    //dummy fraud service for simplicity
    public FraudCheckResponse check(FraudCheckRequest request) {
        if (request.amount().compareTo(MAX_SINGLE_TRANSFER) > 0) {
            log.warn("Fraud detected - high amount. accountId={}, amount={}", request.accountId(), request.amount());
            return new FraudCheckResponse(true, "Amount exceeds limit");
        }

        return new FraudCheckResponse(false, null);
    }
}