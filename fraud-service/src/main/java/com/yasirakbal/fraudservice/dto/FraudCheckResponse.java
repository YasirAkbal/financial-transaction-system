package com.yasirakbal.fraudservice.dto;

public record FraudCheckResponse(
        boolean fraudulent,
        String reason
) {}












