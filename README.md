# Spring Quiz Microservice

A distributed, microservices-based quiz application built using Spring Boot. This project breaks down the core application logic into autonomous services, leveraging the Spring Cloud ecosystem for service discovery, load balancing, dynamic routing, and inter-service communication.

## Architecture Overview

The system consists of the following microservices:
*   **Service Registry**: Built with Netflix Eureka Server to handle dynamic service registration and discovery.
*   **API Gateway**: Acts as a single entry point for all client requests, routing them to the appropriate underlying microservice.
*   **Question Service**: Manages the question bank, handles CRUD operations for quiz questions, filters queries by category, and calculates user scores.
*   **Quiz Service**: Coordinates quiz creation, aggregates question data from the Question Service, tracks snapshots of questions inside specific quizzes, and handles user submissions.

## Features

*   **Service Discovery**: Automated registration of microservices with Eureka.
*   **API Routing**: Client requests are unified and routed via a central API Gateway with load balancer support.
*   **Inter-Service Communication**: Abstracted HTTP client communication handled dynamically through OpenFeign.
*   **Component Snapshotting**: The Quiz Service maintains independent structural mappings (`QuizQuestion`) to accurately preserve state.
*   **Comprehensive Logging**: Full-level tracking for outgoing Feign client calls and extensive SLF4J tracing throughout controllers and services for simplified debugging.

## Technology Stack

*   Java
*   Spring Boot
*   Spring Cloud (Eureka Server, OpenFeign, Spring Cloud Gateway, LoadBalancer)
*   Spring Data JPA
*   MySQL

## API Endpoints

### Question Service
*   `GET /question/allQuestions` - Retrieve all available questions.
*   `GET /question/category/{category}` - Fetch questions filtered by category.
*   `POST /question/getQuestions` - Fetch specific question data securely using a list of IDs inside the request body.
*   `POST /question/getScore` - Evaluate submitted responses and return the final calculated score.

### Quiz Service
*   `POST /quiz/create` - Instantiate a new quiz using specific criteria.
*   `GET /quiz/get/{id}` - Fetch a structured list of questions (with answers hidden) associated with a given quiz.
*   `POST /quiz/submit/{id}` - Submit answers against a specific quiz to calculate performance.

## Setup and Installation

### Prerequisites
*   Java 17 or higher
*   Maven
*   MySQL Server

### Configuration Steps
1. Clone the repository:
   ```bash
   git clone [https://github.com/SharvariShalgar20/spring-quiz-microservice.git](https://github.com/SharvariShalgar20/spring-quiz-microservice.git)
   ```
2. Set up your MySQL databases for both question-service and quiz-service, then update the respective application.properties or application.yml files with your local database credentials.
3. Start the Service Registry (service-registry) application first.
4. Start the API Gateway (api-gateway) application.
5. Spin up the core functional services (question-service and quiz-service).
6. Verify service registration by navigating to your Eureka Dashboard (default: http://localhost:8761).
