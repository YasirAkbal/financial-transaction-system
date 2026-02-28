package com.yasirakbal.accountservice.domain.infrastructure.repository;

import com.yasirakbal.accountservice.domain.infrastructure.idempotency.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, String> {
}