package com.yasirakbal.accountservice.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DebeziumConfig {
    @Bean
    public io.debezium.config.Configuration customerConnector() {
        return io.debezium.config.Configuration.create()
                .with("name", "account-service-outbox-connector")
                .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                .with("database.hostname", "postgres")
                .with("database.port", "5432")
                .with("database.user", "username")
                .with("database.password", "password")
                .with("database.dbname", "account_service")
                .with("database.server.name", "account_service")
                .with("table.include.list", "public.outbox_messages")
                .with("plugin.name", "pgoutput")
                .with("slot.name", "debezium_slot")
                .with("publication.name", "debezium_pub")
                // Debezium nereye kadar okuduğunu buraya kaydeder
                // Uygulama restart olunca kaldığı yerden devam eder
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", "/tmp/debezium-offsets.dat")
                .with("offset.flush.interval.ms", "1000")
                // Schema history — DDL değişikliklerini takip eder
                .with("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory")
                .with("schema.history.internal.file.filename", "/tmp/debezium-schema-history.dat")
                .build();
    }
}