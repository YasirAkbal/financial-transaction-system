package com.yasirakbal.moneytransferservice.application.controller;

import com.yasirakbal.moneytransferservice.application.dto.TransferRequest;
import com.yasirakbal.moneytransferservice.application.dto.TransferResponse;
import com.yasirakbal.moneytransferservice.application.service.TransactionService;
import com.yasirakbal.moneytransferservice.domain.aggregate.Transaction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransferResponse> initiateTransfer(
            @Valid @RequestBody TransferRequest request) {

        Transaction transaction = transactionService.makeTransaction(
                request.sourceAccountId(), request.sourceCustomerId(),
                request.targetAccountId(), request.targetCustomerId(),
                request.amount(), request.currency()
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(TransferResponse.fromEntity(transaction));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransferResponse> getStatus(@PathVariable UUID transactionId) {
        Transaction transaction = transactionService.getTransaction(transactionId);
        return ResponseEntity.ok(TransferResponse.fromEntity(transaction));
    }
}