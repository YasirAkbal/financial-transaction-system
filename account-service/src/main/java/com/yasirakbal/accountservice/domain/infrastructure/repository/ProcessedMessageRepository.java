package com.yasirakbal.accountservice.domain.infrastructure.repository;

import com.yasirakbal.accountservice.domain.infrastructure.idempotency.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, String> {
}