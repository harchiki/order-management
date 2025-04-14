# 🧪 Order Management Demo Project

A microservices-based demo project demonstrating an order and payment workflow.  
Focus areas include inter-service communication patterns (sync & async) and RabbitMQ as the primary message broker for decoupled event handling.

## ⚙️ Tech Stack

| Layer / Tool            | Description                                                                 |
|-------------------------|-----------------------------------------------------------------------------|
| **Java 21**             | Main programming language used in the project, with support for modern features. |
| **Maven**               | Build automation and dependency management tool for Java projects.          |
| **Spring Boot**         | Framework for building stand-alone, production-ready Spring applications.   |
| **Spring JPA**         | Used for simple data access and CRUD operations via repository interfaces.   |
| **Spring Cloud**        | Currently used for centralized configuration management via Config Server. |
| **RabbitMQ**            | Message broker enabling asynchronous communication between microservices.  |
| **PostgreSQL**          | Relational database used for persistent data storage.                       |
| **Redis**               | In-memory data store used for caching and fast data access.                 |
| **Lombok**              | Java library that reduces boilerplate code by generating common methods.    |
| **MapStruct**           | Annotation-based code generator for mapping between Java beans (DTO ↔ Entity). |
| **Jackson (FasterXML)**| Library used to handle JSON serialization and deserialization.              |


---

## 📦 Microservices Overview

| Service Name                         | Description                                                                              | Technologies Used                          | Ports                  |
|--------------------------------------|------------------------------------------------------------------------------------------|--------------------------------------------|------------------------|
| **Config Server**                    | Provides centralized configuration for all microservices.                                | Spring Cloud Config                        | 8888                   |
| **Order Service**                    | Manages order creation, order-related data, and the order validation process.            | Spring Boot, Spring JPA, PostgreSQL, Redis | 8080                   |
| **Accounting Service**               | Handles verified order costs and redirects them to the payment and billing services.     | Spring Boot                                | 8081                   |
| **Product Service**                  | Manages product stock availability and updates stock data.                               | Spring Boot, Spring JPA, PostgreSQL        | 8082                   |
| **Discount Service**                 | Validates and applies discount codes to orders.                                          | Spring Boot                                | 8083                   |
| **Billing Service** (Planned)        | Manages the billing process for verified and completed orders.                           | -                                          | -                      |
| **Payment Service** (Planned)        | Provides the payment processing for validated orders.                                    | -                                          | -                      |

A common module is used to gather shared tools such as Lombok, MapStruct, Jackson (FasterXML), and Spring AMQP for managing RabbitMQ schemas.

## 🔄 Workflow

