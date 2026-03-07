package com.yasirakbal.accountservice.application.forwarder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yasirakbal.accountservice.domain.infrastructure.outbox.DebeziumEnvelope;
import com.yasirakbal.accountservice.domain.infrastructure.outbox.DebeziumOutboxRecord;
import com.yasirakbal.accountservice.domain.infrastructure.outbox.DebeziumOpType;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.data.Struct;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventForwarder {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Configuration customerConnector;
    private DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void start() {
        debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(customerConnector.asProperties())
                .notifying(this::handleEvent)
                .build();

        executor.execute(debeziumEngine);
        log.info("[Debezium] Embedded engine started, listening to outbox_messages");
    }


    private void handleEvent(RecordChangeEvent<SourceRecord> changeEvent) {
        SourceRecord record = changeEvent.record();
        Struct value = (Struct) record.value();

        if (value == null) return; // tombstone mesajı — sil event'i, atla

        String op = value.getString("op");
        if (!"c".equals(op)) return; // sadece INSERT (create) işle

        Struct after = value.getStruct("after");
        if (after == null) return;

        String topic     = after.getString("topic");
        String aggregateId = after.getString("aggregateid"); // PostgreSQL lowercase yapar
        String eventType = after.getString("eventtype");
        String payload   = after.getString("payload");

        log.info("[Debezium] Outbox event caught: type={}, topic={}", eventType, topic);

        kafkaTemplate.send(topic, aggregateId, payload);
    }


    @PreDestroy
    public void stop() {
        if (debeziumEngine != null) {
            try {
                debeziumEngine.close();
                executor.shutdown();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (IOException | InterruptedException e) {
                log.error("[Debezium] Engine shutdown error", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
