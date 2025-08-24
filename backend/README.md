# Mauriexchange Backend 🇲🇷💱

A Spring Boot 3 application that provides REST APIs for currency exchange data in Mauritania, aggregating information from the Central Bank and national banks.

## 🚀 Features

- **Exchange Rates API**: Get current and historical exchange rates
- **Currency Conversion**: Real-time currency conversion with best rates
- **Bank Integration**: Support for multiple Mauritanian banks
- **OpenAPI Documentation**: Auto-generated Swagger/OpenAPI 3.0 docs
- **Caching**: Redis-based caching for improved performance
- **Rate Limiting**: API rate limiting per user/IP
- **Monitoring**: Health checks and metrics via Spring Actuator
- **Containerized**: Docker support for easy deployment

## 🏗️ Tech Stack

- **Java 21 LTS**
- **Spring Boot 3.x**
- **Spring Data JPA** with Hibernate
- **Spring Security** (minimal config for public APIs)
- **MySQL 8.0** (PlanetScale in production)
- **Flyway** for database migrations
- **SpringDoc OpenAPI** for API documentation
- **Caffeine** for caching
- **Docker** for containerization
- **Fly.io** for cloud deployment

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.8+
- MySQL 8.0 (for local development)
- Docker (optional)

## 🛠️ Local Development

### 1. Clone and Setup

```bash
git clone <repository-url>
cd mauriexchange/backend
```

### 2. Database Setup

Start MySQL locally or use Docker:

```bash
# Using Docker
docker run --name mauriexchange-mysql \
  -e MYSQL_ROOT_PASSWORD=rootpass \
  -e MYSQL_DATABASE=mauriexchange_dev \
  -p 3306:3306 \
  -d mysql:8.0
```

### 3. Environment Variables

Create `.env` file in the backend directory:

```bash
# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=mauriexchange_dev
DB_USER=root
DB_PASSWORD=rootpass

# Application
SPRING_PROFILES_ACTIVE=dev
LOG_LEVEL=DEBUG
```

### 4. Run the Application

```bash
# Using Maven
./mvnw spring-boot:run

# Or build and run
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 5. Using Docker

```bash
# Build image
docker build -t mauriexchange-backend .

# Run container
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=3306 \
  -e DB_NAME=mauriexchange_dev \
  -e DB_USER=root \
  -e DB_PASSWORD=rootpass \
  mauriexchange-backend
```

## 🌐 API Endpoints

### Base URL
- Local: `http://localhost:8080/api/v1`
- Production: `https://mauriexchange-backend.fly.dev/api/v1`

### Exchange Rates

```http
# Get exchange rates for a currency pair on a specific date
GET /exchange-rates?fromCurrency=USD&toCurrency=MRU&date=2024-01-15

# Get latest exchange rate for a currency pair
GET /exchange-rates/latest?fromCurrency=USD&toCurrency=MRU

# Get all exchange rates for a specific date
GET /exchange-rates/date/2024-01-15

# Get supported source currencies
GET /exchange-rates/currencies/from

# Get supported target currencies
GET /exchange-rates/currencies/to
```

### Currency Conversion

```http
# Convert currency
POST /convert
Content-Type: application/json

{
  "fromCurrency": "USD",
  "toCurrency": "MRU",
  "amount": 100.00,
  "bankCode": "BCM"  // Optional
}

# Get current exchange rate
GET /convert/rate?fromCurrency=USD&toCurrency=MRU&bankCode=BCM
```

### Health & Monitoring

```http
# Health check
GET /actuator/health

# Application info
GET /actuator/info

# Metrics (Prometheus format)
GET /actuator/prometheus
```

## 📚 API Documentation

Once the application is running, visit:

- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v1/api-docs

## 🧪 Testing

### Run Tests

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Run integration tests
./mvnw verify
```

### Test Coverage

The application includes:
- Unit tests for services and controllers
- Integration tests with TestContainers
- API tests with REST Assured

## 🐳 Docker

### Build Image

```bash
docker build -t mauriexchange-backend .
```

### Run with Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop services
docker-compose down
```

## 🚀 Deployment

### Fly.io Deployment

1. **Install Fly CLI**:
   ```bash
   curl -L https://fly.io/install.sh | sh
   ```

2. **Login to Fly**:
   ```bash
   fly auth login
   ```

3. **Deploy**:
   ```bash
   fly deploy
   ```

### Environment Variables for Production

Set these secrets in Fly.io:

```bash
fly secrets set DATABASE_URL="mysql://user:pass@host:3306/db"
fly secrets set DB_USER="your_db_user"
fly secrets set DB_PASSWORD="your_db_password"
fly secrets set ADMIN_USER="admin"
fly secrets set ADMIN_PASSWORD="secure_password"
```

## 📊 Monitoring

### Health Checks

The application provides health checks at:
- `/actuator/health` - Overall health
- `/actuator/health/liveness` - Liveness probe
- `/actuator/health/readiness` - Readiness probe

### Metrics

Metrics are available in Prometheus format at `/actuator/prometheus`

### Logging

Logs are structured and include:
- Request/response logging
- Performance metrics
- Error tracking
- User activity

## 🔧 Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
# Database
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}

# Caching
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=500,expireAfterWrite=300s

# API Rate Limiting
mauriexchange:
  exchange:
    rate-limiter:
      requests-per-minute: 100
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact: dev@mauriexchange.com
- Documentation: https://mauriexchange.com/docs

## 🔄 CI/CD

The project uses GitHub Actions for continuous integration and deployment:

- **On Pull Request**: Runs tests and security scans
- **On Main Branch**: Builds, tests, and deploys to production
- **On Develop Branch**: Deploys to staging environment

See `.github/workflows/backend-ci-cd.yml` for details.
