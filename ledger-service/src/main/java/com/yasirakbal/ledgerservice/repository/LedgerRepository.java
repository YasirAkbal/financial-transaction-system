package com.yasirakbal.ledgerservice.repository;

import com.yasirakbal.ledgerservice.entity.Ledger;
import com.yasirakbal.ledgerservice.enums.LogType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Ledger> findByCorrelationIdAndLogType(String correlationId, LogType logType);
}
