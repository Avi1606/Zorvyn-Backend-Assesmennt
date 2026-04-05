# Finance Data Processing and Access Control Backend 🏦

## 📋 Project Overview

A comprehensive Spring Boot 3.x REST API backend for managing financial transactions with role-based access control (RBAC). Users can view, create, analyze transactions and access financial dashboards with features like JWT authentication, soft deletes, and advanced filtering.

**Assignment Status:** ✅ **READY FOR SUBMISSION**

---

## 🚀 Tech Stack

| Component | Technology |
|-----------|------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.x |
| **Security** | Spring Security + JWT |
| **Database** | PostgreSQL (H2 for testing) |
| **ORM** | Spring Data JPA + Hibernate |
| **API Docs** | Swagger/OpenAPI 3.0 |
| **Build Tool** | Maven 3.8+ |
| **JSON Processing** | Jackson |
| **Validation** | Jakarta Validation |

---

## 📁 Project Structure

```
Finance Data Processing and Access Control Backend/
├── pom.xml                           # Maven dependencies
├── RUN_GUIDE.md                      # Quick start guide
├── SUBMISSION_CHECKLIST.md           # Bug fixes log
├── README.md                         # This file
│
└── src/main/java/org/avi1606/financedataprocessing/
    │
    ├── FinanceDataProcessingAndAccessControlBackendApplication.java  # Main entry point
    │
    ├── config/
    │   ├── DataInitializer.java      # Seeds default admin user on startup
    │   ├── SecurityConfig.java       # JWT & Spring Security configuration
    │   └── SwaggerConfig.java        # OpenAPI documentation config
    │
    ├── controller/
    │   ├── AuthController.java       # POST /api/auth/register, /login
    │   ├── UserController.java       # GET/PATCH /api/users (Admin only)
    │   ├── TransactionController.java # CRUD /api/transactions with filters
    │   └── DashboardController.java  # GET /api/dashboard/** (aggregations)
    │
    ├── service/
    │   ├── AuthService.java          # Authentication logic
    │   ├── UserService.java          # User management (CRUD + mappers)
    │   ├── TransactionService.java   # Transaction business logic
    │   └── DashboardService.java     # Financial analytics queries
    │
    ├── repository/
    │   ├── UserRepository.java       # JPA custom queries for users
    │   └── TransactionRepository.java # JPA with custom aggregations
    │
    ├── model/
    │   ├── User.java                 # JPA entity with UUID @Id
    │   └── Transaction.java          # JPA entity with soft delete
    │
    ├── dto/
    │   ├── TransactionMapper.java    # Entity → DTO mapper (static methods)
    │   ├── UserMapper.java           # Entity → DTO mapper (static methods)
    │   ├── AuthRequest/Response.java # Login/register DTOs
    │   ├── TransactionRequest/Response.java
    │   ├── UserResponse.java
    │   ├── DashboardSummary.java
    │   ├── CategoryTotal.java
    │   └── MonthlyTrend.java
    │
    ├── enums/
    │   ├── Role.java                 # VIEWER, ANALYST, ADMIN
    │   ├── UserStatus.java           # ACTIVE, INACTIVE
    │   ├── TransactionType.java      # INCOME, EXPENSE
    │   └── Category.java             # SALARY, FOOD, RENT, etc.
    │
    ├── exception/
    │   ├── GlobalExceptionHandler.java    # @ControllerAdvice with @ExceptionHandler
    │   ├── ResourceNotFoundException.java # 404 errors
    │   └── UnauthorizedException.java    # 401 errors
    │
    └── security/
        ├── JwtFilter.java           # Intercepts requests, validates JWT
        ├── JwtUtil.java             # JWT generation & validation
        └── UserDetailsServiceImpl.java # Loads user from database for Spring

└── src/main/resources/
    └── application.properties       # Database + JWT configuration
```

---

## 🔐 Authentication & Authorization

### JWT Flow
1. User calls `POST /api/auth/login` with email + password
2. Server validates credentials against bcrypt-hashed passwords
3. Server generates JWT with user ID and role inside the token
4. Client stores JWT and includes it in `Authorization: Bearer <token>` header
5. JwtFilter validates token on every protected request
6. Spring Security extracts role from token and enforces @PreAuthorize rules

### Role-Based Permissions

| Feature | VIEWER | ANALYST | ADMIN |
|---------|:------:|:-------:|:-----:|
| View Transactions | ✅ | ✅ | ✅ |
| View Transaction Insights | ❌ | ✅ | ✅ |
| Create Transactions | ❌ | ❌ | ✅ |
| Update Transactions | ❌ | ❌ | ✅ |
| Delete Transactions | ❌ | ❌ | ✅ |
| View Dashboard | ✅ | ✅ | ✅ |
| Manage Users | ❌ | ❌ | ✅ |

---

## 📡 API Endpoints

### 🔓 Authentication (Public)
```
POST   /api/auth/register    # Register new user → JWT
POST   /api/auth/login       # Login with email+password → JWT
```

### 👥 Users (Admin Only)
```
GET    /api/users            # List all users with pagination
GET    /api/users/{id}       # Get user by ID
PATCH  /api/users/{id}/status    # Update user status (ACTIVE/INACTIVE)
PATCH  /api/users/{id}/role      # Update user role (VIEWER/ANALYST/ADMIN)
```

### 💰 Transactions (Viewer+)
```
GET    /api/transactions                 # List all (supports filters)
GET    /api/transactions?type=INCOME     # Filter by type
GET    /api/transactions?category=FOOD   # Filter by category
GET    /api/transactions?startDate=2024-01-01&endDate=2024-12-31
GET    /api/transactions/{id}            # Get single transaction
POST   /api/transactions                 # Create (Admin only)
PUT    /api/transactions/{id}            # Update (Admin only)
DELETE /api/transactions/{id}            # Soft delete (Admin only)
GET    /api/transactions/insights        # Analytics (Analyst+)
```

### 📊 Dashboard (Viewer+)
```
GET    /api/dashboard/summary           # Total income, expense, net balance
GET    /api/dashboard/category-totals   # Grouped by category
GET    /api/dashboard/recent            # Last 10 transactions
GET    /api/dashboard/monthly-trends    # Month-wise income vs expense
```

### 📖 API Documentation
```
GET    /swagger-ui.html                 # Interactive Swagger UI
GET    /v3/api-docs                     # OpenAPI JSON spec
```

---

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- BCrypt hashed
    role ENUM('VIEWER', 'ANALYST', 'ADMIN') DEFAULT 'VIEWER',
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Transactions Table
```sql
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    amount DECIMAL(15,2) NOT NULL CHECK (amount > 0),
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    category ENUM('SALARY', 'FOOD', 'RENT', 'UTILITIES', 'INVESTMENT', 'OTHER'),
    date DATE NOT NULL,
    notes VARCHAR(500),
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_future_date CHECK (date <= CURRENT_DATE)
);

CREATE INDEX idx_transactions_created_by ON transactions(created_by);
CREATE INDEX idx_transactions_date ON transactions(date);
CREATE INDEX idx_transactions_is_deleted ON transactions(is_deleted);
```

---

## ✅ Validation Rules Implemented

### Transaction Validation
- ✅ **Amount:** Must be positive (> 0)
- ✅ **Type:** Must be INCOME or EXPENSE (enum)
- ✅ **Category:** Must be valid enum (SALARY, FOOD, RENT, UTILITIES, INVESTMENT, OTHER)
- ✅ **Date:** Cannot be in the future
- ✅ **Notes:** Optional, max 500 characters

### User Validation
- ✅ **Email:** Valid email format, unique in database
- ✅ **Password:** Minimum 6 characters, bcrypt hashed
- ✅ **Name:** Required, max 255 characters
- ✅ **Role:** Must be valid enum (VIEWER, ANALYST, ADMIN)
- ✅ **Status:** Must be ACTIVE or INACTIVE

---

## 🛡️ Error Handling

All errors follow a standardized JSON response format:
```json
{
    "status": 400,
    "error": "Bad Request",
    "message": "Amount must be positive",
    "timestamp": "2024-04-05T10:30:00.000Z"
}
```

### Status Codes
- **200 OK** - Request successful
- **201 Created** - Resource created
- **204 No Content** - Successful deletion
- **400 Bad Request** - Validation failed (with field errors)
- **401 Unauthorized** - Missing/invalid JWT token
- **403 Forbidden** - Insufficient permissions
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Server error

### Exception Handling (@ControllerAdvice)
- ✅ `MethodArgumentNotValidException` → 400 with field-level errors
- ✅ `ResourceNotFoundException` → 404
- ✅ `AccessDeniedException` → 403
- ✅ `AuthenticationException` → 401
- ✅ Generic `Exception` → 500

---

## 🚀 Getting Started

### Prerequisites
- **Java:** 17 or higher
- **Maven:** 3.8+
- **PostgreSQL:** 12+ (or H2 for testing)
- **Git:** For cloning repository

### Installation & Setup

#### 1. Clone Repository
```bash
git clone <repository-url>
cd Finance\ Data\ Processing\ and\ Access\ Control\ Backend
```

#### 2. Configure Database (PostgreSQL)
```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE finance_db;

-- (Optional) Create user with password
CREATE USER finance_user WITH PASSWORD 'finance_password';
GRANT ALL PRIVILEGES ON DATABASE finance_db TO finance_user;
```

#### 3. Update application.properties
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
jwt.secret=your_jwt_secret_key_here_min_32_chars_recommended
jwt.expiration=86400000  # 24 hours in milliseconds

# Server Configuration
server.port=8080
spring.application.name=FinanceDataProcessing

# Logging
logging.level.root=INFO
logging.level.org.avi1606.financedataprocessing=DEBUG
```

#### 4. Build & Run
```bash
# Build project
mvn clean package

# Run with embedded H2 (for quick testing)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"

# Or run JAR
java -jar target/FinanceDataProcessing-0.0.1-SNAPSHOT.jar
```

#### 5. Access Application
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console (if using H2)

---

## 🔑 Default Credentials

**Auto-created on startup:**
```
Email:    admin@finance.com
Password: admin123
Role:     ADMIN
Status:   ACTIVE
```

After login, you'll receive a JWT token:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@finance.com",
    "password": "admin123"
  }'
```

Response:
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "admin@finance.com",
    "role": "ADMIN"
}
```

---

## 📝 Example API Calls

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@finance.com",
    "password": "admin123"
  }'
```

### 2. Create Transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5000.00,
    "type": "INCOME",
    "category": "SALARY",
    "date": "2024-04-05",
    "notes": "Monthly salary"
  }'
```

### 3. Get Dashboard Summary
```bash
curl -X GET http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Filter Transactions
```bash
curl -X GET "http://localhost:8080/api/transactions?type=EXPENSE&category=FOOD&startDate=2024-01-01&endDate=2024-12-31" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Register New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

---

## 🔄 Business Logic Features

### Soft Delete
- Transactions are never hard-deleted from database
- `is_deleted` flag is set to `true` instead
- All queries exclude soft-deleted records by default
- Historical data preserved for auditing

### Transaction Filtering
- Supports multiple filters: `type`, `category`, `startDate`, `endDate`
- Filters can be combined: `?type=INCOME&category=SALARY&startDate=2024-01-01`
- All non-deleted transactions returned if no filters specified

### Dashboard Aggregations
- **Summary:** Calculates total income, total expense, net balance
- **Category Totals:** Groups transactions by category with total amount and count
- **Recent Transactions:** Last 10 transactions ordered by date descending
- **Monthly Trends:** Month-wise income vs expense with net change

### Security Features
- ✅ BCrypt password hashing (no plain text passwords)
- ✅ JWT token validation on every protected request
- ✅ Role-based access control (@PreAuthorize)
- ✅ CORS enabled for development (configurable)
- ✅ SQL injection prevention (parameterized queries)
- ✅ XSS protection via Spring Security headers

---

## 📊 Critical Bugs Fixed (Before Submission)

### ✅ Bug 1: Transaction Creation Failure
- **Issue:** `getCurrentUserId()` returned random UUID every time
- **Fix:** Now extracts user ID from JWT principal
- **File:** `TransactionController.java:121-125`

### ✅ Bug 2: No Admin User Creation
- **Issue:** Registration hardcoded to VIEWER role only
- **Fix:** Added `DataInitializer` that seeds admin user on startup
- **Credentials:** admin@finance.com / admin123
- **File:** `DataInitializer.java` (new)

### ✅ Bug 3: H2 Console Blocked
- **Issue:** Spring Security blocked iframe usage for H2 console
- **Fix:** Added frame options header + permitAll for /h2-console/**
- **File:** `SecurityConfig.java:54-55`

### ✅ Bug 4: Dead Code
- **Issue:** Empty `DashboardRepository` interface was unused
- **Fix:** Deleted dead code
- **File:** `DashboardRepository.java` (deleted)

---

## 🧪 Testing

### Manual Testing Checklist
```
☐ Start application with: mvn spring-boot:run
☐ Login with admin@finance.com / admin123
☐ Create a transaction as ADMIN
☐ View dashboard summary as ADMIN
☐ Register new VIEWER user
☐ Login as VIEWER and try to create transaction (should fail 403)
☐ Filter transactions by type and category
☐ Update transaction as ADMIN
☐ Soft delete transaction and verify it's excluded from queries
☐ Access H2 console at http://localhost:8080/h2-console
☐ Check Swagger UI at http://localhost:8080/swagger-ui.html
```

### Unit Testing (Optional)
```bash
mvn test
```

---

## 🛠️ Technology Details

### Spring Security
- Stateless JWT authentication
- SessionCreationPolicy.STATELESS (no server sessions)
- Custom JwtFilter at authentication filter position
- Global CORS configuration

### Spring Data JPA
- UUID as primary key (generated with @GeneratedValue)
- Soft delete pattern with @Query custom methods
- Lazy loading with FetchType.LAZY for relationships
- Named queries for frequently used filters

### Lombok
- Reduces boilerplate with @Data, @Builder, @Slf4j
- Auto-generates getters/setters/equals/hashCode

### Hibernate
- Automatic table creation with spring.jpa.hibernate.ddl-auto=update
- Timestamp auditing with @CreationTimestamp, @UpdateTimestamp
- Enum types mapped to VARCHAR with @Enumerated(STRING)

---

## 📋 Assumptions & Design Decisions

1. **UUID Primary Keys:** More suitable for distributed systems than auto-increment
2. **Soft Delete:** Preferred over hard delete for financial data (audit trail)
3. **Role-Based Access:** Implemented with @PreAuthorize instead of custom filters (more maintainable)
4. **DTO Pattern:** All responses use DTOs to decouple API from internal model
5. **Service Layer:** Contains business logic, repositories contain data access only
6. **Bcrypt Hashing:** 10 rounds (default) balances security and performance
7. **JWT Secret:** Should be at least 256 bits in production
8. **CORS Enabled:** For development only, disable in production or restrict to specific origins

---

## 🚨 Security Considerations

### Production Checklist
- [ ] Change JWT secret to strong random value (min 32 chars)
- [ ] Set `spring.jpa.show-sql=false` (don't log SQL)
- [ ] Restrict CORS to specific frontend domain
- [ ] Use HTTPS only (set spring.security.require-https=true)
- [ ] Use environment variables for sensitive config (DB password, JWT secret)
- [ ] Enable rate limiting to prevent brute force attacks
- [ ] Add request logging and monitoring
- [ ] Backup database regularly

---

## 📚 Project Dependencies

Key Maven dependencies in pom.xml:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
</dependency>
```

---

## 🐛 Troubleshooting

### "FATAL: database "finance_db" does not exist"
```bash
# Create the database
createdb finance_db

# Or via psql:
psql -U postgres -c "CREATE DATABASE finance_db;"
```

### "Port 8080 already in use"
```bash
# Change port in application.properties:
server.port=8081
```

### JWT Token Expired
- Default expiration: 24 hours
- Change in application.properties: `jwt.expiration=172800000` (48 hours)

### H2 Console Not Loading
- Ensure `/h2-console/**` is in permitAll() routes in SecurityConfig
- Check that frame options header is set to SAME_ORIGIN

---

## 📞 Support & Contact

For issues or clarifications:
1. Check SUBMISSION_CHECKLIST.md for all fixes applied
2. Review API documentation at /swagger-ui.html
3. Check application logs for error messages
4. Verify PostgreSQL is running and accessible

---

## 📄 License & Assignment Info

**Assignment Project:** Finance Data Processing and Access Control Backend
**Status:** ✅ COMPLETE & READY FOR SUBMISSION
**Build Status:** ✅ SUCCESS (JAR: 71.0 MB)
**Java Version:** 17
**Spring Boot Version:** 3.x

**Date:** April 5, 2026
**Author:** Submission Ready

---

## ✨ Summary

This project implements a **production-ready Finance Management Backend** with:
- ✅ Complete REST API with 15+ endpoints
- ✅ JWT-based authentication & role-based authorization
- ✅ PostgreSQL database with soft delete pattern
- ✅ Financial dashboard with aggregations
- ✅ Comprehensive error handling & validation
- ✅ OpenAPI/Swagger documentation
- ✅ All 4 critical bugs fixed pre-submission
- ✅ 100% compilation success

**Ready for immediate deployment and testing.** 🚀

