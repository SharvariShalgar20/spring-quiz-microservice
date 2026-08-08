# Spring Quiz Microservice

A distributed, microservices-based quiz application built using **Java, Spring Boot, Spring Cloud, MySQL, MinIO, and Docker**.

The application follows a microservice architecture where authentication, question management, quiz management, service discovery, and API routing are separated into independent services.

---

## Architecture

The system consists of the following services:

```text
                         ┌──────────────────┐
                         │      Client      │
                         │  Web / Postman   │
                         └────────┬─────────┘
                                  │
                                  ▼
                       ┌─────────────────────┐
                       │     API Gateway     │
                       │      :8060          │
                       └──────────┬──────────┘
                                  │
              ┌───────────────────┼───────────────────┐
              │                   │                   │
              ▼                   ▼                   ▼
     ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
     │  Auth Service   │ │ Question Service│ │   Quiz Service  │
     │     :8082       │ │      :8080      │ │      :8081      │
     └────────┬────────┘ └────────┬────────┘ └────────┬────────┘
              │                   │                   │
              ▼                   ▼                   ▼
        ┌───────────┐       ┌───────────┐       ┌───────────┐
        │ Auth MySQL│       │Question   │       │ Quiz MySQL│
        │   :3309   │       │ MySQL     │       │   :3308   │
        └───────────┘       │   :3307   │       └───────────┘
                            └─────┬─────┘
                                  │
                                  ▼
                            ┌───────────┐
                            │   MinIO   │
                            │ Object    │
                            │ Storage   │
                            │ :9000     │
                            └───────────┘

                       ┌─────────────────────┐
                       │   Eureka Server     │
                       │       :8761         │
                       │ Service Discovery   │
                       └─────────────────────┘
```

### Service Communication

```text
Client
   │
   ▼
API Gateway
   │
   ├──► Auth Service
   │
   ├──► Question Service
   │          │
   │          └──► MinIO
   │
   └──► Quiz Service
              │
              └──► Question Service
                         │
                         └──► Question MySQL
```

---

## Features

### Authentication Service

- User registration and authentication
- Role-based users
- MySQL-based persistence
- Authentication-related APIs

### Question Service

- Create and manage quiz questions
- Retrieve all questions
- Filter questions by category
- Retrieve questions by IDs
- Calculate quiz scores
- Store question images using MinIO object storage

### Quiz Service

- Create quizzes based on categories and difficulty
- Fetch quiz questions
- Hide correct answers from quiz responses
- Maintain question snapshots for quizzes
- Submit quiz answers
- Calculate user scores

### API Gateway

- Single entry point for client requests
- Dynamic routing to microservices
- Integrates with Eureka Service Discovery
- Load balancing support

### Service Registry

- Netflix Eureka Server
- Dynamic service registration and discovery
- Allows services to communicate without hardcoded service locations

### Object Storage

- MinIO is used for storing question images
- Images are stored separately from the relational database
- Question Service communicates with MinIO through its S3-compatible API

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming Language |
| Spring Boot | Microservice Development |
| Spring Cloud | Microservice Infrastructure |
| Spring Cloud Gateway | API Gateway |
| Netflix Eureka | Service Discovery |
| OpenFeign | Inter-Service Communication |
| Spring Cloud LoadBalancer | Client-side Load Balancing |
| Spring Data JPA | Database Access |
| MySQL 8 | Relational Database |
| MinIO | Object Storage |
| Docker | Containerization |
| Docker Compose | Multi-container Orchestration |
| Maven | Build & Dependency Management |
| SLF4J | Application Logging |

---

## Project Structure

```text
spring-quiz-microservice/
│
├── api-gateway/
│   └── ...
│
├── auth-service/
│   └── ...
│
├── question-service/
│   └── ...
│
├── quiz-service/
│   └── ...
│
├── service-registry/
│   └── ...
│
├── docker-compose.yml
│
└── README.md
```

---

## Running the Project with Docker

The project uses **Docker Compose** to run the complete microservice infrastructure.

Docker Compose starts:

- Eureka Service Registry
- API Gateway
- Authentication Service
- Question Service
- Quiz Service
- Three independent MySQL databases
- MinIO Object Storage

### Prerequisites

Install:

- [Docker](https://www.docker.com/)
- Docker Compose
- Git

You do **not** need to install MySQL locally when using the Docker setup.

---

### 1. Clone the Repository

```bash
git clone https://github.com/SharvariShalgar20/spring-quiz-microservice.git
```

```bash
cd spring-quiz-microservice
```

---

### 2. Environment Variables

Sensitive credentials should **not** be committed to GitHub.

Create a `.env` file in the root directory:

```env
MYSQL_ROOT_PASSWORD=your_secure_mysql_password

MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=your_secure_minio_password
```


Add the following to `.gitignore`:

```gitignore
.env
*.env
```

For the public repository, use placeholder values or environment variables instead of real credentials.

---

### 3. Docker Compose Configuration

The Docker Compose setup creates an isolated Docker network:

```text
quiz-network
```

All microservices communicate with each other using their **Docker service names** instead of `localhost`.

For example:

```text
question-service → http://question-mysql:3306
quiz-service     → http://quiz-mysql:3306
auth-service     → http://auth-mysql:3306
question-service → http://minio:9000
```

This is important because inside Docker:

```text
localhost
```

refers to the **current container**, not another container.

---

### 4. Start the Application

From the project root:

```bash
docker compose up --build
```

To run everything in the background:

```bash
docker compose up --build -d
```

Check running containers:

```bash
docker compose ps
```

View logs:

```bash
docker compose logs -f
```

View logs for a particular service:

```bash
docker compose logs -f question-service
```

Stop the application:

```bash
docker compose down
```

To stop containers and remove their associated volumes:

```bash
docker compose down -v
```

> ⚠️ `docker compose down -v` deletes the Docker volumes containing the MySQL and MinIO data.

---

## Service Ports

| Service | Port |
|---|---:|
| Eureka Service Registry | `8761` |
| API Gateway | `8060` |
| Question Service | `8080` |
| Quiz Service | `8081` |
| Auth Service | `8082` |
| MinIO API | `9000` |
| MinIO Console | `9001` |
| Question MySQL | `3307` |
| Quiz MySQL | `3308` |
| Auth MySQL | `3309` |

---

## Database Architecture

Each microservice has its **own database**.

```text
Auth Service
     │
     ▼
auth_service_db


Question Service
     │
     ▼
question_service_db


Quiz Service
     │
     ▼
quiz_service_db
```

This follows the **database-per-service** principle of microservice architecture.

The services do not share a single MySQL database.

---

## MinIO Object Storage

MinIO is used to store question images.

```text
Question Service
       │
       │ S3 API
       ▼
     MinIO
       │
       ▼
question-images bucket
```

MinIO provides an S3-compatible object storage API and is used to store files separately from relational data.

### MinIO Console

After starting Docker Compose, the MinIO console is available at:

```text
http://localhost:9001
```

Use the credentials configured through environment variables.

---

### Creating the Question Images Bucket

Create a bucket called:

```text
question-images
```

The bucket can be configured through the MinIO Console or MinIO Client (`mc`).

For applications where images need to be directly accessible by clients, configure **read-only/public download access** rather than public write access.

Example:

```bash
mc anonymous set download local/question-images
```

This allows objects to be downloaded while preventing anonymous users from uploading or deleting objects.

---

## Inter-Service Communication

The project uses **OpenFeign** for communication between services.

For example:

```text
Quiz Service
     │
     │ OpenFeign
     ▼
Question Service
```

The Quiz Service can request questions from the Question Service without hardcoding the Question Service's container IP address.

Eureka provides service discovery:

```text
Quiz Service
     │
     ▼
Eureka Server
     │
     ▼
Question Service
```

---
## Architecture Principles

This project demonstrates several important microservice architecture concepts:

- **Independent services**
- **Database per service**
- **Service discovery**
- **API Gateway pattern**
- **Inter-service communication**
- **Client-side load balancing**
- **Containerization**
- **Object storage**
- **Centralized Docker network**
- **Environment-based configuration**
- **Independent service deployment**
- **JWT authentication**

---
# 👩‍💻 Author

**Sharvari Shalgar**
