package com.yasirakbal.accountservice.shared.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDateTime;
import java.util.*;

@MappedSuperclass
public abstract class BaseAggregateRoot<T extends BaseAggregateRoot<T>> extends AbstractAggregateRoot<T> {

    @Id
    @Getter
    private UUID id;

    @Getter
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Getter
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected BaseAggregateRoot() {
        this.id = UUID.randomUUID();
    }

    protected BaseAggregateRoot(UUID id) {
        this.id = id;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseAggregateRoot<?> that = (BaseAggregateRoot<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}