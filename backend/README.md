# MauriExchange Backend API

A Spring Boot REST API for managing currency exchange data, built with modern Java practices and comprehensive OpenAPI documentation.

## 🚀 Features

- **RESTful API**: Clean, RESTful endpoints for currency data access
- **OpenAPI/Swagger Documentation**: Interactive API documentation
- **JSON Data Source**: Reads from local JSON file with automatic parsing
- **Comprehensive Error Handling**: Graceful error handling with proper HTTP status codes
- **Flexible Querying**: Multiple ways to search and filter currency data
- **Modern Java**: Built with Spring Boot 3.5.5 and Java 17

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Access to the JSON data source file

## 🛠️ Setup and Installation

1. **Clone the repository** (if not already done):
   ```bash
   git clone <repository-url>
   cd mauriexchange/backend
   ```

2. **Configure the data source path** in `application.properties`:
   ```properties
   app.data.source.path=../database/your-data-source.json
   ```

3. **Build the application**:
   ```bash
   mvn clean install
   ```

4. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## 📚 API Documentation

Once the application is running, you can access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 🔗 API Endpoints

### Base URL
```
http://localhost:8080/api/v1/currencies
```

### Available Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all currencies |
| GET | `/paginated?page={page}&size={size}` | Get all currencies with pagination |
| GET | `/{id}` | Get currency by ID |
| GET | `/code/{code}` | Get currency by ISO code |
| GET | `/search?name={name}` | Search currencies by name |
| GET | `/search/paginated?name={name}&page={page}&size={size}` | Search currencies by name with pagination |
| GET | `/{currencyId}/exchange-rates/latest?limit={limit}` | Get latest exchange rates |
| GET | `/{currencyId}/exchange-rates/range?startDate={date}&endDate={date}` | Get exchange rates by date range |

### Example Requests

#### Get All Currencies
```bash
curl -X GET "http://localhost:8080/api/v1/currencies"
```

#### Get Currency by Code
```bash
curl -X GET "http://localhost:8080/api/v1/currencies/code/{CURRENCY_CODE}"
```

#### Search Currencies by Name
```bash
curl -X GET "http://localhost:8080/api/v1/currencies/search?name={CURRENCY_NAME}"
```

#### Get Latest Exchange Rates
```bash
curl -X GET "http://localhost:8080/api/v1/currencies/{CURRENCY_ID}/exchange-rates/latest?limit={LIMIT}"
```

#### Get Exchange Rates by Date Range
```bash
curl -X GET "http://localhost:8080/api/v1/currencies/{CURRENCY_ID}/exchange-rates/range?startDate={START_DATE}&endDate={END_DATE}"
```

#### Get All Currencies with Pagination
```bash
curl -X GET "http://localhost:8080/api/v1/currencies/paginated?page={PAGE}&size={SIZE}"
```

#### Search Currencies by Name with Pagination
```bash
curl -X GET "http://localhost:8080/api/v1/currencies/search/paginated?name={CURRENCY_NAME}&page={PAGE}&size={SIZE}"
```

## 📊 Response Format

All API responses follow a consistent format:

```json
{
  "success": true,
  "message": "Successfully retrieved currencies",
  "data": [...],
  "timestamp": "2024-01-01T00:00:00"
}
```

### Currency Response Structure
```json
{
  "id": 1,
  "nameFr": "Currency Name in French",
  "nameAr": "اسم العملة بالعربية",
  "unity": 100,
  "code": "XXX",
  "createdAt": "2024-01-01T00:00:00.000",
  "updatedAt": "2024-01-01T00:00:00.000",
  "publishedAt": "2024-01-01T00:00:00.000",
  "exchangeRates": [
    {
      "id": 1,
      "day": "2024-01-01",
      "value": "100.00",
      "createdAt": "2024-01-01T00:00:00.000",
      "updatedAt": "2024-01-01T00:00:00.000",
      "publishedAt": "2024-01-01T00:00:00.000",
      "endDate": "2024-01-02"
    }
  ]
}
```

### Paginated Response Structure
```json
{
  "success": true,
  "message": "Successfully retrieved currencies with pagination",
  "data": {
    "data": [
      {
        "id": 1,
        "nameFr": "Currency Name in French",
        "nameAr": "اسم العملة بالعربية",
        "unity": 100,
        "code": "XXX",
        "createdAt": "2024-01-01T00:00:00.000",
        "updatedAt": "2024-01-01T00:00:00.000",
        "publishedAt": "2024-01-01T00:00:00.000",
        "exchangeRates": [...]
      }
    ],
    "metadata": {
      "page": 0,
      "size": 10,
      "totalElements": 100,
      "totalPages": 10,
      "hasNext": true,
      "hasPrevious": false,
      "isFirst": true,
      "isLast": false
    }
  },
  "timestamp": "2024-01-01T00:00:00"
}
```

## 🏗️ Project Structure

```
backend/
├── src/main/java/com/mauriexchange/code/
│   ├── config/           # Configuration classes
│   ├── controller/       # REST controllers
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # Data models
│   ├── exception/       # Custom exceptions
│   ├── service/         # Business logic
│   │   └── impl/        # Service implementations
│   └── BackendApplication.java
├── src/main/resources/
│   └── application.properties
└── src/test/java/       # Test classes
```

## 🧪 Testing

Run the tests with:
```bash
mvn test
```

The tests verify:
- Application context loading
- Currency data retrieval
- Currency search functionality

## 🔧 Configuration

### Application Properties

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Server port | 8080 |
| `app.data.source.path` | Path to JSON data source | `../database/your-data-source.json` |
| `springdoc.swagger-ui.path` | Swagger UI path | `/swagger-ui.html` |
| `app.pagination.default-page-size` | Default page size for pagination | 20 |
| `app.pagination.max-page-size` | Maximum allowed page size | 100 |

## 🚨 Error Handling

The API provides comprehensive error handling:

- **404 Not Found**: When currency or data is not found
- **500 Internal Server Error**: For data processing errors
- **400 Bad Request**: For invalid request parameters

All errors return a consistent format:
```json
{
  "success": false,
  "message": "Error description",
  "timestamp": "2024-01-20T10:30:00"
}
```

## 🔒 Security Considerations

- Input validation for all parameters
- Proper error handling without exposing sensitive information
- CORS configuration for web client access

## 📈 Performance

- Efficient JSON parsing with Jackson
- Stream-based data processing
- Optional caching for frequently accessed data

## 🤝 Contributing

1. Follow the existing code structure and patterns
2. Add comprehensive tests for new features
3. Update documentation for any API changes
4. Use meaningful commit messages

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
