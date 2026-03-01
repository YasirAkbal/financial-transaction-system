package com.yasirakbal.ledgerservice.controller;

import com.yasirakbal.ledgerservice.dto.LedgerResponse;
import com.yasirakbal.ledgerservice.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    @GetMapping
    public List<LedgerResponse> getAllLogs() {
        return ledgerService.getAllLogs();
    }
}