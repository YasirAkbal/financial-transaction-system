package common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class BaseIntegrationEvent {
    protected final UUID id;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    protected final LocalDateTime occurredOn;

    protected final String correlationId;
    protected final String eventType;

    @JsonCreator
    protected BaseIntegrationEvent(
            @JsonProperty("id") UUID id,
            @JsonProperty("occurredOn") LocalDateTime occurredOn,
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("eventType") String eventType) {
        this.id = id;
        this.occurredOn = occurredOn;
        this.correlationId = correlationId;
        this.eventType = eventType;
    }

    protected BaseIntegrationEvent(String correlationId) {
        this.id = UUID.randomUUID();
        this.occurredOn = LocalDateTime.now();
        this.correlationId = correlationId;
        this.eventType = this.getClass().getSimpleName();
    }
}