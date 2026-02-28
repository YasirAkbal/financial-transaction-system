package com.yasirakbal.accountservice.domain.infrastructure.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

    private final ProcessedMessageRepository processedMessageRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String transactionId, OperationType operationType, String serviceName) {
        processedMessageRepository.saveAndFlush(ProcessedMessage.builder()
                .transactionId(transactionId)
                .operationType(operationType)
                .serviceName(serviceName)
                .processedAt(LocalDateTime.now())
                .build());
    }
}