package com.yasirakbal.fraudservice.controller;

import com.yasirakbal.fraudservice.dto.FraudCheckRequest;
import com.yasirakbal.fraudservice.dto.FraudCheckResponse;
import com.yasirakbal.fraudservice.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
public class FraudController {

    private final FraudDetectionService fraudDetectionService;

    @PostMapping("/check")
    public FraudCheckResponse check(@RequestBody FraudCheckRequest request) {
        return fraudDetectionService.check(request);
    }
}