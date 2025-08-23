# 3-Tier Web Application Stack Specification

## Project Overview
Modern 3-tier web application with separated concerns, containerized development environment, and cloud-native deployment strategy.

## Architecture Components

### 1. Frontend - Angular Application
**Framework**: Angular 18+ (latest stable)
**Language**: TypeScript 5.x
**Deployment**: Vercel
**Directory**: `/frontend`

#### Technology Stack
- **UI Framework**: Angular Material / PrimeNG
- **State Management**: NgRx or Akita
- **HTTP Client**: Angular HttpClient with interceptors
- **Code Generation**: OpenAPI Generator for TypeScript/Angular
- **Styling**: SCSS with CSS Variables for theming
- **Build Tool**: Angular CLI with esbuild
- **Testing**: 
  - Unit: Jasmine + Karma
  - E2E: Cypress or Playwright
  - Component: Angular Testing Library

#### Development Features
- Hot Module Replacement (HMR)
- Environment-specific configurations
- Lazy loading modules
- PWA capabilities (optional)
- i18n support structure

### 2. Backend - Spring Boot Application
**Framework**: Spring Boot 3.x
**Language**: Java 21 LTS
**Deployment**: Fly.io
**Directory**: `/backend`

#### Technology Stack
- **API Framework**: Spring Web MVC with REST
- **API Documentation**: SpringDoc OpenAPI (Swagger 3.0)
- **Database Access**: Spring Data JPA with Hibernate
- **Validation**: Jakarta Bean Validation
- **Security**: Spring Security (minimal config, public endpoints)
- **Build Tool**: Maven or Gradle
- **Testing**:
  - Unit: JUnit 5 + Mockito
  - Integration: Spring Boot Test with TestContainers
  - API: REST Assured
  - Contract: Spring Cloud Contract (optional)

#### API Features
- RESTful API design
- OpenAPI 3.0 specification auto-generation
- CORS configuration for local development
- Request/Response logging
- Global exception handling
- API versioning strategy

### 3. Database - MySQL
**Database**: MySQL 8.0
**Cloud Provider**: PlanetScale
**Directory**: `/database`

#### Configuration
- **Local Development**: MySQL 8.0 in Docker
- **Migration Tool**: Flyway or Liquibase
- **Schema**: Versioned migration scripts
- **Seeding**: Development data scripts

## Project Structure
```
webappstack/
├── frontend/
│   ├── src/
│   │   ├── app/
│   │   ├── assets/
│   │   ├── environments/
│   │   └── styles/
│   ├── generated/
│   │   └── api/  # OpenAPI generated code
│   ├── Dockerfile
│   ├── angular.json
│   ├── package.json
│   └── tsconfig.json
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/yourcompany/app/
│   │   │   │       ├── controller/
│   │   │   │       ├── service/
│   │   │   │       ├── repository/
│   │   │   │       ├── entity/
│   │   │   │       ├── dto/
│   │   │   │       ├── mapper/
│   │   │   │       ├── config/
│   │   │   │       └── exception/
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-dev.yml
│   │   │       └── db/migration/
│   │   └── test/
│   ├── Dockerfile
│   ├── pom.xml (or build.gradle)
│   └── openapi.yaml
│
├── database/
│   ├── migrations/
│   │   └── V1__initial_schema.sql
│   ├── seeds/
│   │   └── development_data.sql
│   ├── Dockerfile
│   └── init.sql
│
├── docker-compose.yml
├── docker-compose.override.yml  # Local overrides
├── .env.example
├── .gitignore
└── README.md
```

## API Contract & Code Generation

### OpenAPI Specification
- **Location**: `/backend/openapi.yaml` or auto-generated at `/v3/api-docs`
- **Version**: OpenAPI 3.0
- **Tools**: 
  - Backend: SpringDoc OpenAPI for auto-generation from annotations
  - Frontend: OpenAPI Generator CLI for TypeScript/Angular services

### Code Generation Workflow
1. Backend defines APIs with Spring annotations
2. SpringDoc generates OpenAPI spec at runtime
3. Build process exports spec to static file
4. Frontend build generates TypeScript interfaces and Angular services
5. Pre-commit hooks ensure contract synchronization

### Generated Code Structure
```typescript
// Frontend generated code example
frontend/generated/api/
├── models/
│   ├── user.ts
│   └── product.ts
├── services/
│   ├── user.service.ts
│   └── product.service.ts
└── index.ts
```

## Docker Configuration

### Frontend Dockerfile
```dockerfile
# Multi-stage build
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist/frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
```

### Backend Dockerfile
```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose Services
```yaml
version: '3.8'
services:
  frontend:
    build: ./frontend
    ports:
      - "4200:80"
    environment:
      - API_URL=http://localhost:8080
    
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DB_HOST=database
    depends_on:
      - database
    
  database:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=rootpass
      - MYSQL_DATABASE=appdb
    volumes:
      - db_data:/var/lib/mysql
      - ./database/init.sql:/docker-entrypoint-initdb.d/init.sql

volumes:
  db_data:
```

## Testing Strategy

### Frontend Testing
1. **Unit Tests**: 
   - Components, Services, Pipes
   - Coverage target: 80%
   - Run: `ng test`

2. **Integration Tests**:
   - Component interaction
   - Service integration with mock backend
   
3. **E2E Tests**:
   - Critical user journeys
   - Cross-browser testing
   - Run: `npm run e2e`

### Backend Testing
1. **Unit Tests**:
   - Service layer logic
   - Utility classes
   - Coverage target: 80%
   - Run: `mvn test`

2. **Integration Tests**:
   - Repository layer with H2/TestContainers
   - REST controller tests with MockMvc
   - Run: `mvn verify`

3. **Contract Tests**:
   - OpenAPI spec validation
   - Request/Response schema validation

### Database Testing
- Migration scripts validation
- Rollback procedures
- Performance testing for queries

## Deployment Configuration

### Frontend - Vercel
```json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist/frontend",
  "framework": "angular",
  "regions": ["iad1"],
  "env": {
    "API_URL": "@backend-api-url"
  }
}
```

### Backend - Fly.io
```toml
# fly.toml
app = "your-backend-app"
primary_region = "iad"

[build]
  image = "your-backend:latest"

[env]
  PORT = "8080"
  SPRING_PROFILES_ACTIVE = "production"

[services]
  internal_port = 8080
  protocol = "tcp"

[[services.ports]]
  port = 443
  handlers = ["tls", "http"]
```

### Database - PlanetScale
- **Branch Strategy**: Main + Development branches
- **Connection**: Via DATABASE_URL with connection pooling
- **Migrations**: Applied via CI/CD pipeline
- **Backups**: Automated daily backups

## Environment Variables

### Development (.env.local)
```bash
# Frontend
VITE_API_URL=http://localhost:8080
VITE_ENV=development

# Backend
SPRING_PROFILES_ACTIVE=dev
DB_HOST=localhost
DB_PORT=3306
DB_NAME=appdb
DB_USER=root
DB_PASSWORD=rootpass

# Database
MYSQL_ROOT_PASSWORD=rootpass
MYSQL_DATABASE=appdb
```

### Production (Managed by deployment platforms)
- Frontend: Vercel environment variables
- Backend: Fly.io secrets
- Database: PlanetScale connection string

## CI/CD Pipeline

### GitHub Actions Workflow
1. **On Pull Request**:
   - Run linting (ESLint, Prettier, Checkstyle)
   - Run unit tests
   - Build applications
   - Validate OpenAPI contract

2. **On Main Branch**:
   - Run full test suite
   - Build Docker images
   - Deploy to staging
   - Run E2E tests
   - Deploy to production (manual approval)

## Security Considerations

1. **Public API Access**: 
   - All endpoints publicly accessible (no auth required initially)
   - Authentication/authorization can be added later when needed
2. **Data Protection**: 
   - HTTPS everywhere
   - Input validation
   - SQL injection prevention via JPA
   - XSS protection
3. **Secrets Management**:
   - Environment variables for database credentials
   - Never commit secrets
4. **CORS**: Properly configured for frontend domain
5. **Rate Limiting**: API throttling to prevent abuse (recommended even for public APIs)

## Development Workflow

1. **Initial Setup**:
   ```bash
   git clone <repository>
   cp .env.example .env
   docker-compose up -d
   ```

2. **Backend Development**:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

3. **Frontend Development**:
   ```bash
   cd frontend
   npm install
   npm start
   ```

4. **API Contract Update**:
   ```bash
   cd backend
   ./mvnw spring-boot:run  # Generates OpenAPI spec
   cd ../frontend
   npm run generate-api     # Generates TypeScript code
   ```

## Monitoring & Observability

1. **Application Monitoring**:
   - Frontend: Vercel Analytics
   - Backend: Fly.io metrics + Custom APM

2. **Logging**:
   - Structured JSON logging
   - Centralized log aggregation
   - Log levels: ERROR, WARN, INFO, DEBUG

3. **Health Checks**:
   - Backend: `/actuator/health`
   - Database: Connection pool monitoring

## Best Practices Checklist

- [ ] Semantic versioning for releases
- [ ] Comprehensive README documentation
- [ ] Code review process established
- [ ] Branch protection rules configured
- [ ] Dependency vulnerability scanning
- [ ] Regular dependency updates
- [ ] Performance budgets defined
- [ ] Accessibility standards (WCAG 2.1 AA)
- [ ] Internationalization support
- [ ] Error tracking integration
- [ ] Database backup strategy
- [ ] Disaster recovery plan
- [ ] API rate limiting
- [ ] Input validation on both frontend and backend
- [ ] Proper error handling and user feedback

## Next Steps

1. Validate this specification with stakeholders
2. Set up repository structure
3. Initialize each project with base configuration
4. Configure CI/CD pipelines
5. Set up deployment environments
6. Create first API endpoint with full stack integration
7. Set up monitoring and alerting
8. Add authentication/authorization when needed (future phase)