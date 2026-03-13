# Architectural Decisions – Subscription Processor

This document explains the key architectural decisions made in the **subscription-processor** service.

---

# 1. Event-Driven Architecture

The system follows an **event-driven architecture**.

Instead of processing requests synchronously, the system uses **RabbitMQ** to decouple event ingestion from event processing.

Benefits:

* loose coupling between services
* asynchronous processing
* better scalability
* resilience to temporary service failures

The processor acts as the **domain processing engine**.

---

# 2. Separation of Publisher and Processor

The system was split into two services:

* notification-api
* subscription-processor

This separation ensures:

* HTTP handling remains lightweight
* domain processing can scale independently
* failures in the processor do not block the API

---

# 3. Domain-Oriented Structure

The service structure was designed to keep **domain logic separated from infrastructure concerns**.

Layers were organized as follows:

* `domain` – core business concepts
* `application` – orchestration logic
* `infrastructure` – messaging and HTTP integrations

This improves maintainability and testability.

---

# 4. Use Case + Service Pattern

The system uses a **Use Case + Service** approach.

### Use Case

Represents a specific application workflow.

Example:

```text
ProcessNotificationUseCase
```

Responsibilities:

* orchestrate processing steps
* call domain services

---

### Service

Contains reusable business logic.

Example:

```text
SubscriptionService
```

Responsibilities:

* resolve target status
* create or update subscription
* persist event history

This separation prevents use cases from becoming overly complex.

---

# 5. Event History Tracking

The system stores every processed event in the `event_history` table.

Benefits:

* auditing capability
* ability to inspect processing order
* debugging support
* traceability

---

# 6. Message Error Handling Strategy

The consumer differentiates between **business errors** and **technical errors**.

### Business Errors

Examples:

* subscription not found for cancellation
* invalid event type

These errors are handled in the consumer and **do not trigger message retry**.

This prevents **infinite reprocessing loops**.

---

### Technical Errors

Examples:

* database failures
* infrastructure problems

These errors are allowed to propagate, enabling retry mechanisms.

---

# 7. Enum-Based Event Validation

Event types are validated using an enum:

```text
EventType
```

A static `from()` method converts the string payload to the enum safely.

Invalid values throw a domain exception.

---

# 8. Pagination for History Queries

Event history endpoints support pagination using Spring Data's `Pageable`.

Benefits:

* avoids large result sets
* improves performance
* supports sorting and filtering

Example query:

```text
GET /subscriptions/{id}/history?page=0&size=10&sort=processedAt,desc
```

---

# 9. Testing Strategy

Unit tests focus on validating **business behavior rather than framework behavior**.

Tests include:

* domain rule validation
* event processing flows
* consumer error handling
* repository interaction behavior

This ensures the system behaves correctly under different scenarios.

---

# 10. Design Principles Applied

The following engineering principles guided the implementation:

* Separation of concerns
* Event-driven architecture
* Domain-focused design
* Asynchronous processing
* Resilient error handling
* Testable business logic

These principles improve long-term maintainability and scalability of the system.
