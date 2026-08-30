# Quiz App Microservices

A full-featured quiz application built to learn and demonstrate core microservices architecture patterns using Spring Boot and Spring Cloud — service discovery, API gateway routing, inter-service communication, stateless JWT authentication with RBAC, object storage, and event-driven communication via Kafka.

## Architecture Overview

```
                              ┌─────────────────┐
                              │  API Gateway     │  :8060
                              │  (Spring Cloud    │
                              │   Gateway MVC)    │
                              └─────────┬─────────┘
                                        │
        ┌───────────────┬──────────────┼──────────────┬───────────────┐
        ▼               ▼              ▼               ▼               ▼
┌───────────────┐ ┌────────────┐ ┌────────────┐ ┌───────────────┐ ┌────────────────┐
│ auth-service   │ │ question-  │ │ quiz-      │ │ notification- │ │ analytics-      │
│ :8082/8083     │ │ service    │ │ service    │ │ service       │ │ service         │
│                │ │ :8080      │ │ :8081      │ │ :8084         │ │ :8085           │
└───────┬────────┘ └─────┬──────┘ └─────┬──────┘ └───────┬───────┘ └────────┬────────┘
        │                │              │                │                  │
        ▼                ▼              ▼                └────────┬─────────┘
  ┌───────────┐    ┌───────────┐  ┌───────────┐                   ▼
  │ auth-mysql │    │ question- │  │ quiz-mysql │            ┌──────────┐
  └───────────┘    │ mysql     │  └─────┬──────┘            │  Kafka    │
                    └─────┬─────┘        │                   │ (KRaft)   │
                          │              │  publishes         │  :9092    │
                          ▼              │  QuizSubmittedEvent └────┬─────┘
                    ┌───────────┐         └───────────────────────►│
                    │  MinIO    │                                   │
                    │  :9000/1  │              consumed by ─────────┴──► notification-service
                    └───────────┘                                       analytics-service ──► analytics-mysql

                    ┌─────────────────────┐
                    │  Service Registry    │  :8761  (Eureka)
                    │  All services         │
                    │  register here        │
                    └─────────────────────┘
```

Every service registers with **Eureka** for service discovery. Inter-service HTTP calls (`quiz-service → question-service`) go through **Feign**, load-balanced automatically across any registered instances. All client traffic enters through the **API Gateway**, which resolves routes via Eureka rather than hardcoded hosts.

## Services

| Service | Port | Responsibility | Database |
|---|---|---|---|
| `service-registry` | 8761 | Eureka service discovery | — |
| `api-gateway` | 8060 | Single entry point, routes to all services | — |
| `auth-service` | 8082/8083 | User registration, login, JWT issuance | `auth_service_db` |
| `question-service` | 8080 | Question CRUD, image upload, scoring logic | `question_service_db` |
| `quiz-service` | 8081 | Quiz creation, submission, history, leaderboard | `quiz_service_db` |
| `notification-service` | 8084 | Kafka consumer — simulated notifications on quiz submission | — |
| `analytics-service` | 8085 | Kafka consumer — aggregated quiz stats | `analytics_service_db` |
| `minio` | 9000 (API), 9001 (console) | S3-compatible object storage for question images | — |
| `kafka` | 9092 | Event broker (KRaft mode, no ZooKeeper) | — |

Each service owns its own database — no service reaches into another service's data directly. All cross-service communication happens over HTTP (Feign) or asynchronously via Kafka events.

## Tech Stack

- **Java 17**, **Spring Boot 3.5.x**, **Maven**
- **Spring Cloud 2025.0.3** — Eureka (service discovery), OpenFeign (inter-service HTTP calls), Gateway MVC (routing)
- **Spring Security + JWT** (`jjwt` 0.12.6) — stateless authentication, role-based access control
- **Spring Data JPA + MySQL 8.0** — one database per service
- **Spring Kafka** — event-driven communication (`apache/kafka`, KRaft mode)
- **MinIO** — S3-compatible object storage for question images
- **Springdoc OpenAPI 2.8.5** — Swagger UI on every REST-exposing service
- **Docker + Docker Compose** — full containerized orchestration
- **Lombok**, **SLF4J** — boilerplate reduction, structured logging

## Features

- **Service discovery & load balancing** — multiple instances of any service can run simultaneously; Feign + Eureka distribute requests automatically
- **API Gateway** — single entry point (`:8060`) for all client traffic
- **JWT authentication** — stateless, signed with HS256, validated independently by each service (no central auth check per request)
- **Role-based access control (RBAC)** — `ADMIN` vs `USER` permissions enforced per-endpoint
- **Image upload for questions** — stored in MinIO (S3-compatible), only the URL persisted in MySQL
- **Quiz history & leaderboard** — per-user attempt history, per-quiz leaderboard ranking
- **Event-driven architecture** — quiz submissions publish a Kafka event consumed independently by both a notification service and an analytics service, with zero coupling back to `quiz-service`
- **Swagger/OpenAPI docs on every service** — `auth-service`, `question-service`, `quiz-service`, and `analytics-service` all expose interactive, annotated API documentation
- **Fully containerized** — one `docker-compose up` boots the entire system

## Prerequisites

- Docker & Docker Compose
- (For local, non-Docker development only) Java 17, Maven, MySQL 8.0, a running Kafka broker, MinIO

## Running the Project

### 1. Configure environment variables

Create a `.env` file in the project root:

```dotenv
MYSQL_ROOT_PASSWORD=your_mysql_password
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=your_minio_password
```

### 2. Start everything

```bash
docker-compose up --build
```

First run takes several minutes (building 7 service images, initializing 4 MySQL databases). Subsequent runs are much faster.

### 3. Verify

- Eureka dashboard: `http://localhost:8761` — confirm all services are registered
- MinIO console: `http://localhost:9001` — login with your `.env` credentials

## API Documentation (Swagger)

Every REST-exposing service has interactive Swagger UI, fully annotated with `@Tag`, `@Operation`, and `@Parameter` descriptions:

| Service | Swagger UI |
|---|---|
| `question-service` | `http://localhost:8080/swagger-ui.html` |
| `quiz-service` | `http://localhost:8081/swagger-ui.html` |
| `auth-service` | `http://localhost:8082/swagger-ui.html` (check your compose port mapping — may be `:8083`) |
| `analytics-service` | `http://localhost:8085/swagger-ui.html` |

`notification-service` is a Kafka-consumer-only service with no REST endpoints, so it has no Swagger UI.

Raw OpenAPI specs are available at `/v3/api-docs` on each of the four services above.

## Core API Flow (via Gateway, port 8060)

```
1. Register / Login
   POST /auth/register   { username, password, role }
   POST /auth/login      { username, password }
   → returns a JWT

2. Create a question (ADMIN only, optional image)
   POST /question/create-question-with-image
   Authorization: Bearer <admin token>
   multipart/form-data: question (JSON part), file (image, optional)

3. Create a quiz
   POST /quiz/create?category=X&numQ=N&title=Y
   Authorization: Bearer <token>

4. Take the quiz
   GET /quiz/get/{quizId}          → questions without right answers

5. Submit answers
   POST /quiz/submit/{quizId}
   Authorization: Bearer <token>
   [{ id, response }, ...]
   → returns score, saves attempt, publishes Kafka event

6. View history / leaderboard
   GET /quiz/history                    (Authorization required)
   GET /quiz/leaderboard/{quizId}        (public)

7. View analytics (populated asynchronously via Kafka)
   GET /analytics/quiz/{quizId}
   GET /analytics/all
```

## Security Model

- Passwords hashed with **BCrypt**, never stored in plain text
- JWTs signed with a shared **HS256 secret** across `auth-service`, `question-service`, and `quiz-service` — each service validates signatures independently, without calling `auth-service` per request
- **Public endpoints**: viewing questions/quizzes, leaderboards
- **Authenticated endpoints**: creating quizzes, submitting answers, viewing history
- **ADMIN-only endpoints**: creating/uploading questions

## Database-per-Service

Each service owns an isolated MySQL database — no shared schemas, no cross-service joins. Services that need data from another domain (e.g., `quiz-service` needing question content) fetch it over HTTP via Feign, never by querying another service's database directly.

## Event-Driven Flow (Kafka)

```
quiz-service (producer)
    │  publishes QuizSubmittedEvent on every submission
    ▼
Kafka topic: quiz-submissions
    │
    ├──► notification-service   (logs a simulated notification)
    └──► analytics-service      (persists rolling stats: attempts, avg score, high score)
```

Both consumers use **separate consumer groups**, so each independently receives every event — this is the fan-out pattern that lets you add more consumers later without touching `quiz-service` at all.

## Project Structure

```
quiz-app-microservices/
├── .env
├── .gitignore
├── docker-compose.yml
├── service-registry/
├── api-gateway/
├── auth-service/
├── question-service/
├── quiz-service/
├── notification-service/
└── analytics-service/
```

Each service directory contains its own `Dockerfile`, `pom.xml`, and standard Maven project structure.

## Known Limitations / Future Work

- No refresh-token flow — JWTs expire after 1 hour with no renewal path
- No global `@ControllerAdvice` exception handling yet — some errors still return raw Spring stack traces instead of clean JSON
- No circuit breaker / retry logic (Resilience4j) — a downstream service outage currently surfaces as a raw Feign exception
- No distributed tracing (Zipkin/Micrometer) — hard to visualize a request's full path across services
- No CI/CD pipeline
- No frontend — this is currently a backend-only system, tested via Postman/Swagger UI

## License

Personal learning project.
