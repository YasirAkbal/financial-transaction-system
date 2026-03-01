# Financial Transaction System

A microservices-based banking backend built with Spring Boot, implementing distributed money transfers via the **Saga pattern** (orchestration-based).

---

## Architecture Overview

```
                                Client → Gateway (8080) → Service Discovery (Eureka)
                                                        ↓
                                         ┌──────────────────────────────────┐
                                         │         Business Services        │
                                         │                                  │
                                         │  customer-service         :8082  │
                                         │  account-service          :8081  │
                                         │  money-transfer-service   :8084  │
                                         │  ledger-service           :8083  │
                                         │  notification-service     :8085  │
                                         │  fraud-service            :8086  │
                                         └──────────────────────────────────┘
                                                        ↓
                                        Kafka (event bus) + PostgreSQL + Redis
```

---

## Services

| Service | Port | Responsibility |
|---|---|---|
| `gateway` | 8080 | API gateway, request routing |
| `discovery` | 8761 | Eureka service registry |
| `config-server` | 8888 | Centralized configuration |
| `customer-service` | 8082 | Customer CRUD |
| `account-service` | 8081 | Account management, debit/credit |
| `money-transfer-service` | 8084 | Transfer orchestration (Saga coordinator) |
| `ledger-service` | 8083 | Immutable transaction log |
| `notification-service` | 8085 | Transfer event notifications |
| `fraud-service` | 8086 | Fraud detection |

---

## Key Concepts

### Orchestration Saga — Money Transfer Flow

Money transfers are executed as a distributed saga where `money-transfer-service` acts as the **central coordinator**. It knows the full flow, issues commands to `account-service`, and reacts to the results — `account-service` just executes what it's told.

```
POST /api/v1/transfers
        │
        ▼
[money-transfer] fraud check → save Transaction(PENDING) → publish DebitCommand
        │
        ▼ (Kafka: transfer-commands)
[account-service] debit source account → publish AccountDebitedEvent
        │
        ▼ (Kafka: account-events)
[money-transfer] receive debit success → publish CreditCommand
        │
        ▼ (Kafka: transfer-commands)
[account-service] credit target account → publish AccountCreditedEvent
        │
        ▼ (Kafka: account-events)
[money-transfer] Transaction(COMPLETED) → publish MoneyTransferCompletedEvent
```

**Compensation (rollback) on credit failure:**
```
AccountCreditFailed → CompensateDebitCommand → AccountDebitCompensated
                                                → Transaction(FAILED)
```

### Idempotency

Every Kafka consumer operation is guarded by a `processed_messages` table (`transactionId + operationType` unique constraint). Duplicate messages are silently skipped, making all operations safe to retry.

### Pessimistic Locking

Account balance updates use `SELECT ... FOR UPDATE` to prevent race conditions under concurrent transfers.

### Fraud Detection

`money-transfer-service` calls `fraud-service` via HTTP (WebClient) before initiating a transfer. Results are cached in Redis for 5 minutes. A Resilience4j circuit breaker with retry is wrapped around the call — if the fraud service is unavailable, the transaction is **declined by default** (fail-safe).

### Domain-Driven Design

`account-service` and `money-transfer-service` follow a DDD structure:
- **Aggregates** (`Account`, `Transaction`) encapsulate business rules and emit domain events
- **Value Objects** (`Money`) enforce invariants (non-negative amounts, currency matching)
- **Domain Events** → published to Kafka via Spring's `@TransactionalEventListener(AFTER_COMMIT)`

### Correlation ID

Every HTTP request receives an `X-Correlation-Id` header (generated if absent). This ID propagates through all Kafka commands and events, tying the entire saga flow together for tracing and idempotency.

---

## Infrastructure

| Component | Purpose |
|---|---|
| PostgreSQL | Per-service databases (isolated schemas) |
| Apache Kafka (KRaft mode) | Async event bus between services |
| Redis | Fraud check result cache |
| Eureka | Service discovery |

---

## Running Locally

```bash
docker-compose up --build
```

Services start in dependency order. The gateway is available at `http://localhost:8080`.

**Example flow:**
```bash
# 1. Create a customer
POST http://localhost:8082/api/v1/customers

# 2. Create accounts
POST http://localhost:8081/api/v1/accounts

# 3. Initiate transfer
POST http://localhost:8084/api/v1/transfers

# 4. Check status
GET http://localhost:8084/api/v1/transfers/{transactionId}

# 5. View ledger
GET http://localhost:8080/api/v1/ledger
```

---

## What's Coming

- **Outbox Pattern** — Replace `@TransactionalEventListener` with a proper transactional outbox to guarantee at-least-once delivery even on application crashes between DB commit and Kafka publish
- **Observability** — Distributed tracing (OpenTelemetry/Zipkin), structured logging, and metrics (Micrometer/Prometheus)
- **Tests** — Unit tests for domain logic, integration tests with Testcontainers
- **Config & Gateway** — Config server to serve per-service YAMLs from a Git repo; gateway to add auth, rate limiting, and request validation
- **Discovery** — Eureka configured for production-grade peer replication

---

## Tech Stack

Java 21 · Spring Boot 3.3 · Spring Cloud 2023 · Apache Kafka · PostgreSQL · Redis · Resilience4j · Docker Compose · Lombok
