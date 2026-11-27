# Online Shop API

A backend application developed as part of Spring Practice. The project implements an online shop with product management, warehouse stock management and order processing.

## Technologies

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Lombok
- Docker Compose
- Maven
- JUnit

## Prerequisites

- Java 21
- Docker Desktop
- Git

## Getting Started

1. Clone the repository.
2. Create a `.env` file using `.env.example` as a template.
3. Configure the PostgreSQL environment variables.
4. Start PostgreSQL:

   `docker compose up -d`

5. Start the application on Windows:

   `.\mvnw.cmd spring-boot:run`

The application starts on port 8080.

## Database

Flyway manages database migrations automatically during application startup. Hibernate validates the entity mappings against the database schema.

The database contains the following business tables:

- product_category
- product
- location
- users
- orders
- stock
- order_detail

## Running Tests

Start the local PostgreSQL container and execute:

`.\mvnw.cmd clean test`

## Development Status

- Spring Boot project configuration
- PostgreSQL and Docker configuration
- Flyway database migrations
- JPA entities and relationships
- Composite primary keys
- Spring Data JPA repositories
- Persistence integration tests

REST API endpoints, order fulfillment strategies and authentication will be implemented in subsequent training chapters.

## Git Workflow

Development takes place on the `main` branch.