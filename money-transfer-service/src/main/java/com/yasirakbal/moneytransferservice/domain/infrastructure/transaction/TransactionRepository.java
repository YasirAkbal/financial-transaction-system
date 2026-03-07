package com.yasirakbal.moneytransferservice.domain.infrastructure.transaction;

import com.yasirakbal.moneytransferservice.domain.aggregate.Transaction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Transaction> findByCorrelationId(String correlationId);
}
