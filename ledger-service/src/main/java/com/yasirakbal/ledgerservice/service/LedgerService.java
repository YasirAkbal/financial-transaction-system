package com.yasirakbal.ledgerservice.service;

import com.yasirakbal.ledgerservice.entity.Ledger;
import com.yasirakbal.ledgerservice.repository.LedgerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class LedgerService {
    private final LedgerRepository ledgerRepository;

    @Transactional
    public void saveLog(Ledger ledger) {
        String corrId = ledger.getCorrId();

        var existing = ledgerRepository.findByCorrelationIdAndLogType(corrId, ledger.getLogType());
        if(existing.isPresent()) {
            log.info("Transaction already logged for corrId: {}", corrId);
            throw new IllegalArgumentException("The request is already processed");
        }

        ledgerRepository.save(ledger);
    }

}
