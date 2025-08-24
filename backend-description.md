# Mauriexchange Backend 🇲🇷💱

Mauriexchange **Backend** is a Spring Boot 3 service that powers the Mauriexchange platform.
It provides secure, reliable, and scalable **REST APIs** for managing **currency exchange data in Mauritania**, aggregating information from the Central Bank and national banks, and enabling currency conversion features.

---

## ✨ Features

* 📊 **Exchange Rates Aggregation**: Collects and normalizes data from multiple banks/APIs
* 🔄 **Currency Calculator API**: Instant conversion between supported currencies
* 🔧 **API Documentation**: OpenAPI/Swagger 3.0 auto-generated docs
* 🐳 **Containerized Backend**: Docker + Docker Compose for local development
* 🚀 **Cloud Deployment**: Hosted on Fly.io for scalable, cloud-native infrastructure
* 🧪 **Comprehensive Testing**: Unit, integration, contract, and API tests with Testcontainers
* 📈 **Monitoring & Observability**: Spring Actuator, logs, and Fly.io metrics

---

## 🏗️ Tech Stack

* **Language**: Java 21 LTS
* **Framework**: Spring Boot 3

  * Spring Web (REST APIs)
  * Spring Data JPA (Database access)
  * Spring Security (Authentication & Authorization)
  * SpringDoc OpenAPI (API Documentation)
* **Database**: MySQL 8.0 on [PlanetScale](https://planetscale.com)
* **Migrations**: Flyway / Liquibase
* **Testing**: JUnit 5, Mockito, REST Assured, Testcontainers
* **Deployment**: [Fly.io](https://fly.io)

---

## 📂 Project Structure

```
backend/
├── src/main/java/com/mauriexchange/   # Application source
│   ├── controller/                    # REST controllers
│   ├── service/                       # Business logic
│   ├── repository/                    # Spring Data JPA repositories
│   ├── config/                        # Security & App configs
│   └── MauriexchangeApplication.java  # Entry point
├── src/test/java/com/mauriexchange/   # Tests
├── pom.xml                            # Maven configuration
└── Dockerfile                         # Backend container
```

---

## 🚀 Getting Started (Backend Only)

### 1. Setup environment variables

Copy and update `.env.example`:

```bash
cp .env.example .env
```

### 2. Run backend locally

```bash
cd backend
./mvnw spring-boot:run
```

### 3. Run with Docker

```bash
docker-compose up -d backend
```

### 4. API Documentation

Once running, visit:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔄 Development Workflow

* **API-first development**: APIs defined in OpenAPI spec → Frontend consumes generated client
* **CI/CD (GitHub Actions)**:

  * On PR → Lint, Unit Tests, Integration Tests
  * On main → Full test suite, Build Docker image, Deploy to Fly.io

---

## 🛡️ Security
* Rate limiter for each user/client/browser independently
* Input validation & global exception handling
* SQL injection prevention via JPA
* Secrets managed via environment variables

---

## 📊 Monitoring & Observability

* **Spring Boot Actuator** → `/health`, `/metrics`
* **Fly.io metrics & logging**
* **Database Monitoring** → PlanetScale dashboards

---

## 📌 Roadmap (Backend)

* [ ] Implement first bank API integration
* [ ] Add exchange calculator API endpoint
* [ ] Configure CI/CD pipelines for backend
* [ ] Deploy MVP backend to Fly.io
* [ ] Add JWT authentication & role-based access control
* [ ] Caching layer for exchange rates

---

## 📖 License

MIT License

---
