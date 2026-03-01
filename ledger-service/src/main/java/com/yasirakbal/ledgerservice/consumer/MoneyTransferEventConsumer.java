package com.yasirakbal.ledgerservice.consumer;

import com.yasirakbal.ledgerservice.entity.Ledger;
import com.yasirakbal.ledgerservice.enums.LogType;
import com.yasirakbal.ledgerservice.service.LedgerService;
import common.event.MoneyTransferCompletedIntegrationEvent;
import common.event.MoneyTransferFailedIntegrationEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "transfer-events", groupId = "ledger-group")
public class MoneyTransferEventConsumer {
    private final LedgerService ledgerService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaHandler
    public void onMoneyTransferSucceed(MoneyTransferCompletedIntegrationEvent event) {
        Ledger ledger = Ledger.builder()
                .corrId(event.getCorrelationId())
                .logType(LogType.MONEY_TRANSFER_COMPLETED)
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .occurredOn(event.getOccurredOn())
                .sourceAccountId(event.getSourceAccountId())
                .targetAccountId(event.getTargetAccountId())
                .build();

        ledgerService.saveLog(ledger);
    }

    @KafkaHandler
    public void onMoneyTransferFailed(MoneyTransferFailedIntegrationEvent event) {
        Ledger ledger = Ledger.builder()
                .corrId(event.getCorrelationId())
                .logType(LogType.MONEY_TRANSFER_FAILED)
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .occurredOn(event.getOccurredOn())
                .sourceAccountId(event.getSourceAccountId())
                .targetAccountId(event.getTargetAccountId())
                .errorCode(event.getErrorCode())
                .errorMessage(event.getErrorMessage())
                .failedStep(event.getFailedStep())
                .build();

        ledgerService.saveLog(ledger);
    }
}
