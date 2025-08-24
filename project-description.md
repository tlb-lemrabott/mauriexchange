# Mauriexchange 🇲🇷💱

Mauriexchange is a modern web application that centralizes and provides up-to-date **currency exchange rates in Mauritania**.  
It aggregates data from the Central Bank and national banks’ APIs, offering users a **clear view of exchange prices** and a **currency calculator** to convert amounts between currencies with ease.

---

## ✨ Features
- 📊 **Centralized Exchange Rates**: Aggregated from multiple banks and APIs  
- 🔄 **Currency Calculator**: Instant conversions between currencies  
- 🐳 **Containerized Development**: Docker & Docker Compose for consistent local environments  
- 🚀 **Cloud-Native Deployment**: Vercel (frontend), Fly.io (backend), PlanetScale (database)  
- 🔧 **CI/CD Pipelines**: Automated testing, linting, builds, and deployments with GitHub Actions  
- 🧪 **Comprehensive Testing**: Unit, integration, contract, and end-to-end tests  
- 📈 **Monitoring & Observability**: Logs, metrics, and health checks for production reliability  

---

## 🏗️ Tech Stack

### Frontend (Angular 18+)
- **Language**: TypeScript  
- **Framework**: Angular CLI, Angular Material/PrimeNG  
- **State Management**: NgRx / Akita  
- **Testing**: Jasmine + Karma, Cypress/Playwright  
- **Deployment**: [Vercel](https://vercel.com)  

### Backend (Spring Boot 3, Java 21 LTS)
- **Framework**: Spring Boot (REST APIs, Spring Data JPA, Spring Security)  
- **API Docs**: SpringDoc OpenAPI (Swagger 3.0)  
- **Testing**: JUnit 5, Mockito, Testcontainers, REST Assured  
- **Deployment**: [Fly.io](https://fly.io)  

### Database
- **Engine**: MySQL 8.0  
- **Cloud Provider**: [PlanetScale](https://planetscale.com)  
- **Migrations**: Flyway / Liquibase  

---

## 📂 Project Structure
```

mauriexchange/
├── frontend/       # Angular 18+ app
├── backend/        # Spring Boot 3 REST API
├── database/       # Migrations & seeds
├── docker-compose.yml
└── README.md

````

---

## 🚀 Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/<your-username>/mauriexchange.git
cd mauriexchange
````

### 2. Setup environment variables

Copy and update `.env.example`:

```bash
cp .env.example .env
```

### 3. Start services locally

```bash
docker-compose up -d
```

### 4. Run frontend (Angular)

```bash
cd frontend
npm install
npm start
```

### 5. Run backend (Spring Boot)

```bash
cd backend
./mvnw spring-boot:run
```

---

## 🔄 Development Workflow

* **API-first development**: Backend defines APIs → OpenAPI spec → Frontend generates TypeScript clients
* **GitHub Actions CI/CD**:

  * On PR: Lint + Unit Tests + Build + Contract Validation
  * On main: Full test suite + Build Docker images + Deploy to staging → Production

---

## 🛡️ Security & Best Practices

* HTTPS everywhere
* SQL injection prevention via JPA
* Input validation & exception handling
* Environment-based secrets (never hardcoded)
* Rate limiting (planned)

---

## 📊 Monitoring & Observability

* **Frontend**: Vercel Analytics
* **Backend**: Fly.io metrics + Spring Actuator `/health`
* **Database**: PlanetScale automated backups

---

## 📌 Roadmap

* [ ] Implement first currency API integration
* [ ] Add exchange calculator UI
* [ ] Configure CI/CD pipelines
* [ ] Deploy MVP to Vercel + Fly.io + PlanetScale
* [ ] Add authentication/authorization (future phase)
* [ ] Multi-language (i18n) support

---

## 💸 Cost Strategy

This system is designed to stay under **\$50/month**:

* Vercel (Frontend) → Free tier or minimal plan
* Fly.io (Backend) → Pay-as-you-go with small instances
* PlanetScale (Database) → Free tier with scaling options
* GitHub Actions → Free tier for CI/CD

---

## 📖 License

MIT License – free to use and modify.

---

## 👨‍💻 Author

Built by **Lemrabott Toulba** a **Software Engineer passionate about scalable distributed systems** using **Java Spring Boot + Angular** to create production-grade, cost-efficient solutions.