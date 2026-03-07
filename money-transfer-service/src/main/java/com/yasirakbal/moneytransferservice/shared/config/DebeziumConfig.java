package com.yasirakbal.moneytransferservice.shared.config;

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
                .with("database.dbname", "money_transfer_service")
                .with("topic.prefix", "money_transfer_service")
                .with("table.include.list", "public.outbox_messages")
                .with("plugin.name", "pgoutput")
                .with("slot.name", "money_transfer_slot")
                .with("publication.name", "money_transfer_pub")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", "/tmp/debezium-offsets.dat")
                .with("offset.flush.interval.ms", "1000")
                .with("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory")
                .with("schema.history.internal.file.filename", "/tmp/debezium-schema-history.dat")
                .build();
    }
}