# Subscription Processor

## Overview

The **Subscription Processor** is responsible for consuming subscription events from RabbitMQ and applying business rules to update the system state.

This service represents the **core domain processing component** of the system.

It receives events asynchronously, processes them according to business rules, and persists subscription state and event history in PostgreSQL.

---

## Responsibilities

The service is responsible for:

* Consuming events from RabbitMQ
* Interpreting subscription events
* Applying business rules
* Updating subscription status
* Persisting event history
* Providing read endpoints for subscription queries

---

## Architecture

The service follows a **layered architecture inspired by Clean Architecture principles**.

```text
subscription-processor
├── application
│   ├── dto
│   │   └── response
│   ├── mapper
│   ├── service
│   └── usecase
│
├── config
│
├── domain
│   ├── entity
│   ├── enums
│   └── repository
│
├── exception
│
└── infrastructure
    ├── messaging
    │   └── consumer
    └── web
        └── controller
```

### Layer Responsibilities

**domain**

Contains core domain concepts:

* entities
* enums
* repositories

This layer contains **business concepts independent of infrastructure**.

**application**

Contains application orchestration:

* use cases
* services
* DTO mappings

**infrastructure**

Contains technical integrations:

* RabbitMQ consumers
* REST controllers
* database interaction through Spring Data

---

## Event Processing Flow

Events are consumed from RabbitMQ and processed through the application layers.

```text
RabbitMQ
   │
   ▼
NotificationConsumer
   │
   ▼
ProcessNotificationUseCase
   │
   ▼
SubscriptionService
   │
   ├── Update subscription state
   └── Persist event history
```

---

## Business Rules

The system supports the following events:

| Event                  | Behavior                             |
| ---------------------- | ------------------------------------ |
| SUBSCRIPTION_PURCHASED | Creates a new subscription           |
| SUBSCRIPTION_CANCELED  | Cancels an existing subscription     |
| SUBSCRIPTION_RESTARTED | Reactivates an existing subscription |

### Rules enforced

* `SUBSCRIPTION_PURCHASED` creates a subscription if it does not exist.
* `SUBSCRIPTION_CANCELED` requires an existing subscription.
* `SUBSCRIPTION_RESTARTED` requires an existing subscription.
* Every event generates an entry in `event_history`.

---

## Database Model

### Status

```text
status
------
id
name
```

Values:

* ACTIVE
* CANCELED

---

### Subscription

```text
subscription
------------
id
status_id
created_at
updated_at
```

---

### Event History

```text
event_history
-------------
id
subscription_id
event_type
processed_at
```

---

## Query API

The processor exposes endpoints for querying subscription data.

### Get subscription

```text
GET /subscriptions/{id}
```

Example response:

```json
{
  "id": "sub_123",
  "status": "ACTIVE",
  "createdAt": "2026-03-12T17:00:00",
  "updatedAt": "2026-03-12T17:05:00"
}
```

---

### Get subscription history

Supports pagination and sorting.

```text
GET /subscriptions/{id}/history?page=0&size=5&sort=processedAt,desc
```

Example response:

```json
{
  "content": [
    {
      "id": 1,
      "eventType": "SUBSCRIPTION_PURCHASED",
      "processedAt": "2026-03-12T17:00:00"
    }
  ]
}
```

---

## Error Handling

The service distinguishes between:

### Business Errors

Examples:

* Invalid event type
* Subscription not found for cancellation
* Subscription not found for restart

These are handled in the consumer to prevent infinite message retries.

---

### Technical Errors

Examples:

* Database failures
* Infrastructure issues

These are allowed to propagate so that retry mechanisms can occur.

---

## Testing

Unit tests were implemented to cover the following scenarios:

* Create subscription when purchase event occurs
* Cancel existing subscription
* Restart canceled subscription
* Reject cancel/restart for non-existing subscription
* Prevent infinite retry loops
* Validate event type parsing

Tests focus on **business logic correctness and message handling behavior**.

---

## Running the Service

Start infrastructure:

```bash
docker compose up -d
```

Run the service:

```bash
./mvnw spring-boot:run
```

---

## Technology Stack

* Java 21
* Spring Boot
* Spring Data JPA
* RabbitMQ
* PostgreSQL
* Flyway
* MapStruct
* Lombok
* JUnit 5
* Mockito
