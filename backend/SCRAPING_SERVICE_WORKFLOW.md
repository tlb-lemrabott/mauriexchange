# Scraping Service Integration Workflow

## 🎯 Overview

This document outlines the architecture, workflow, and implementation steps for integrating the scraping service with the Mauriexchange backend. The scraping service will fetch exchange rate data from various sources and populate the `mauriexchange_db` database, which the backend API will then serve to clients.

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Web Sources   │    │  Scraping        │    │   Backend       │
│   (Banks, APIs) │───▶│  Service         │───▶│   API           │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │                        │
                                ▼                        ▼
                       ┌──────────────────┐    ┌─────────────────┐
                       │  mauriexchange_db│    │   Frontend      │
                       │  (MySQL)         │    │   (Angular)     │
                       └──────────────────┘    └─────────────────┘
```

## 🔄 Data Flow

1. **Scraping Service** fetches data from multiple sources:
   - Central Bank of Mauritania (BCM)
   - National banks (BNM, BMCI, BABM, SGM)
   - External APIs (if available)

2. **Data Processing**:
   - Normalize exchange rates
   - Validate data integrity
   - Handle different currencies and formats

3. **Database Storage**:
   - Insert/update exchange rates in `mauriexchange_db`
   - Maintain historical data
   - Track data sources and timestamps

4. **API Serving**:
   - Backend reads from the same database
   - Provides RESTful APIs to frontend
   - Implements caching for performance

## 📋 Implementation Steps

### Phase 1: Database Schema Preparation ✅

**Status: COMPLETED**

- [x] Create initial database schema with tables:
  - `banks` - Bank information
  - `exchange_rates` - Exchange rate data
  - `currency_conversions` - Conversion tracking
  - `api_requests` - API monitoring

- [x] Insert baseline data:
  - Mauritanian banks (BCM, BNM, BMCI, BABM, SGM)
  - Sample exchange rates for testing

**Rationale**: This provides the minimum viable data structure that both the scraping service and backend can work with.

### Phase 2: Scraping Service Development

**Status: PENDING**

#### 2.1 Service Architecture
```python
# Recommended Tech Stack
- Python 3.11+
- FastAPI (for API endpoints)
- BeautifulSoup4 (web scraping)
- Requests (HTTP client)
- SQLAlchemy (database ORM)
- Celery (task scheduling)
- Docker (containerization)
```

#### 2.2 Core Components
```
scraping-service/
├── src/
│   ├── scrapers/
│   │   ├── bcm_scraper.py
│   │   ├── bnm_scraper.py
│   │   ├── bmci_scraper.py
│   │   └── base_scraper.py
│   ├── models/
│   │   ├── exchange_rate.py
│   │   └── bank.py
│   ├── database/
│   │   ├── connection.py
│   │   └── migrations/
│   ├── services/
│   │   ├── data_processor.py
│   │   └── scheduler.py
│   └── api/
│       └── endpoints.py
├── tests/
├── docker-compose.yml
└── requirements.txt
```

#### 2.3 Data Sources Priority
1. **Central Bank of Mauritania (BCM)**
   - Primary source for official rates
   - Daily updates
   - Most reliable

2. **National Banks**
   - BNM (Banque Nationale de Mauritanie)
   - BMCI (Banque Mauritanienne pour le Commerce International)
   - BABM (Banque Al Baraka Mauritanie)
   - SGM (Société Générale Mauritanie)

3. **External APIs** (Future)
   - Currency conversion APIs
   - International rate providers

### Phase 3: Integration Strategy

#### 3.1 Database Sharing
- Both services connect to the same `mauriexchange_db`
- Scraping service: WRITE operations
- Backend service: READ operations
- Use database transactions for data consistency

#### 3.2 Data Synchronization
```sql
-- Example: Exchange rate update workflow
BEGIN TRANSACTION;
  -- Scraping service inserts new rates
  INSERT INTO exchange_rates (bank_id, from_currency, to_currency, buy_rate, sell_rate, rate_date, rate_timestamp, source)
  VALUES (1, 'USD', 'MRU', 35.50, 35.80, CURDATE(), NOW(), 'BCM_SCRAPER');
  
  -- Backend immediately sees new data
  SELECT * FROM exchange_rates WHERE rate_date = CURDATE();
COMMIT;
```

#### 3.3 Error Handling
- Scraping failures don't affect backend API
- Backend serves cached/last-known data
- Monitoring and alerting for scraping issues

### Phase 4: Deployment Architecture

#### 4.1 Docker Compose Setup
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root@root
      MYSQL_DATABASE: mauriexchange_db
    
  backend:
    build: ./backend
    depends_on: [mysql]
    
  scraping-service:
    build: ./scraping-service
    depends_on: [mysql]
    environment:
      - DB_HOST=mysql
      - DB_NAME=mauriexchange_db
```

#### 4.2 Scheduling
- **Development**: Manual scraping triggers
- **Production**: Automated scheduling (every 2-4 hours)
- **Monitoring**: Health checks and failure alerts

## 🎯 Recommended Approach

### Option A: Create Scraping Service First (RECOMMENDED)

**Pros:**
- Populate database with real data before backend development
- Test data quality and consistency
- Identify data structure issues early
- Backend can be developed with realistic data

**Cons:**
- Longer initial development time
- Need to handle scraping challenges upfront

**Implementation Order:**
1. ✅ Backend API (Current - serves as data consumer)
2. 🔄 Scraping Service (Next - populates database)
3. 🔄 Frontend (Last - consumes backend API)

### Option B: Use Current Schema (ALTERNATIVE)

**Pros:**
- Faster initial development
- Backend can be tested immediately
- Clear API contract established

**Cons:**
- May need schema changes after scraping service
- Data quality issues discovered later
- Potential refactoring required

## 📊 Data Quality Considerations

### 1. Rate Validation
```python
def validate_exchange_rate(rate_data):
    # Check for reasonable rate ranges
    if rate_data['buy_rate'] <= 0 or rate_data['sell_rate'] <= 0:
        raise ValueError("Invalid rate values")
    
    # Check for logical buy/sell spread
    if rate_data['sell_rate'] <= rate_data['buy_rate']:
        raise ValueError("Sell rate should be higher than buy rate")
    
    # Check for extreme variations
    # ... additional validation logic
```

### 2. Data Consistency
- Track data source and timestamp
- Handle missing or incomplete data
- Implement data versioning
- Monitor data freshness

### 3. Error Recovery
- Retry mechanisms for failed scrapes
- Fallback to cached data
- Alerting for persistent failures
- Data quality metrics

## 🔧 Technical Implementation

### 1. Scraping Service Features
- **Modular scrapers** for each bank
- **Rate limiting** to avoid being blocked
- **Data validation** before storage
- **Error logging** and monitoring
- **Scheduling** for regular updates

### 2. Database Considerations
- **Indexing** for fast queries
- **Partitioning** for historical data
- **Backup** strategies
- **Monitoring** for performance

### 3. API Integration
- **Caching** for frequently accessed data
- **Rate limiting** for API consumers
- **Versioning** for API changes
- **Documentation** with OpenAPI

## 📈 Monitoring and Observability

### 1. Metrics to Track
- Scraping success rate
- Data freshness
- API response times
- Error rates
- Database performance

### 2. Alerts
- Scraping failures
- Data quality issues
- API downtime
- Database connectivity issues

## 🚀 Next Steps

1. **Immediate**: Complete backend API development
2. **Short-term**: Design scraping service architecture
3. **Medium-term**: Implement scraping service
4. **Long-term**: Optimize and scale the system

## 📝 Conclusion

The recommended approach is to **create the scraping service first** to populate the database with real data. This ensures:

- Data quality from the start
- Realistic testing environment
- Better user experience
- Reduced refactoring needs

The current backend implementation provides a solid foundation that can immediately consume data once the scraping service is operational.
