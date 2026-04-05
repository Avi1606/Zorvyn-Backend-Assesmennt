# Finance Data Processing Backend - Run Guide

## ✅ Current Status

Your project is now **fully compiled and ready to run** with the following configuration:

### Database Configuration
- **Type**: H2 In-Memory Database (for development/testing)
- **Connection**: `jdbc:h2:mem:finance_db`
- **Auto-Schema**: `create-drop` (creates tables on startup, drops on shutdown)
- **H2 Console**: Available at `http://localhost:8080/h2-console`

### Compilation Status
✅ **BUILD SUCCESS** - All 36 source files compiled without errors

---

## How to Run

### Option 1: Run with Maven (Recommended for Development)
```bash
cd "D:\FS Project\Finance Data Processing and Access Control Backend"
mvn spring-boot:run
```

**Expected Output** (look for this line):
```
Started FinanceDataProcessingAndAccessControlBackendApplication in X seconds
```

### Option 2: Build JAR and Run
```bash
# Build the JAR
mvn clean package -DskipTests

# Run the JAR
java -jar target/FinanceDataProcessing-0.0.1-SNAPSHOT.jar
```

---

## Accessing the Application

Once the application is running, access it at:

| Resource | URL |
|----------|-----|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **API Docs** | http://localhost:8080/v3/api-docs |
| **H2 Console** | http://localhost:8080/h2-console |

### H2 Console Login
- **Driver Class**: `org.h2.Driver`
- **JDBC URL**: `jdbc:h2:mem:finance_db`
- **User Name**: `sa`
- **Password**: (leave blank)

---

## API Endpoints (All Endpoints Available)

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### Users (Admin Only)
- `GET /api/users` - List all users
- `PATCH /api/users/{id}/status` - Update user status
- `PATCH /api/users/{id}/role` - Update user role

### Transactions
- `POST /api/transactions` - Create transaction (Admin only)
- `GET /api/transactions` - List transactions with filters
- `GET /api/transactions/{id}` - Get single transaction
- `PUT /api/transactions/{id}` - Update transaction (Admin only)
- `DELETE /api/transactions/{id}` - Soft delete transaction (Admin only)

### Dashboard
- `GET /api/dashboard/summary` - Get financial summary
- `GET /api/dashboard/category-totals` - Get category breakdown
- `GET /api/dashboard/recent` - Get recent transactions
- `GET /api/dashboard/monthly-trends` - Get monthly trends

---

## Testing the API with cURL

### 1. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "ADMIN",
    "status": "ACTIVE"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Response** (save the token):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "john@example.com",
  "role": "ADMIN"
}
```

### 3. Create a Transaction (with JWT token)
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "amount": 5000.00,
    "type": "INCOME",
    "category": "SALARY",
    "date": "2026-04-05",
    "notes": "Monthly salary"
  }'
```

### 4. Get Dashboard Summary
```bash
curl -X GET http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## JWT Token Information

The application uses JWT (JSON Web Token) for authentication:

- **Token Expiration**: 24 hours (86400000 ms)
- **Secret Key**: Located in `application.properties` under `jwt.secret`
- **Token Claims**: 
  - `userId` (subject)
  - `email`
  - `role` (VIEWER, ANALYST, or ADMIN)

**Security Notes**:
- Always send the token in the `Authorization: Bearer <token>` header
- The JWT filter validates every protected request
- Invalid or expired tokens will return 401 Unauthorized

---

## Database Schema (Auto-Created)

### Users Table
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY,
  name VARCHAR(255),
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  role ENUM (VIEWER, ANALYST, ADMIN),
  status ENUM (ACTIVE, INACTIVE),
  created_at TIMESTAMP
);
```

### Transactions Table
```sql
CREATE TABLE transactions (
  id UUID PRIMARY KEY,
  amount DECIMAL(19,2),
  type ENUM (INCOME, EXPENSE),
  category ENUM (SALARY, FOOD, RENT, UTILITIES, INVESTMENT, OTHER),
  date DATE,
  notes VARCHAR(255),
  created_by UUID FOREIGN KEY,
  created_at TIMESTAMP,
  is_deleted BOOLEAN DEFAULT FALSE
);
```

---

## Role-Based Access Control

| Role | Permissions |
|------|-------------|
| **VIEWER** | View transactions, View dashboard |
| **ANALYST** | VIEWER + View transaction insights |
| **ADMIN** | Full access: Create, Update, Delete transactions + Manage users |

---

## Troubleshooting

### Issue: Port 8080 already in use
Change the port in `application.properties`:
```properties
server.port=8081
```

### Issue: H2 tables not created
Ensure `spring.jpa.hibernate.ddl-auto=create-drop` is set in `application.properties`

### Issue: JWT validation errors
1. Make sure token is sent in header: `Authorization: Bearer <token>`
2. Check token hasn't expired (24 hours)
3. Verify user role has permission for the endpoint

### Issue: Cannot connect to H2 Console
- Ensure `spring.h2.console.enabled=true` in application.properties
- Access at: `http://localhost:8080/h2-console`

---

## Switching to PostgreSQL (Production)

To use PostgreSQL instead of H2:

1. Install PostgreSQL and create database:
```sql
CREATE DATABASE finance_db;
```

2. Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

3. Rebuild and run:
```bash
mvn clean compile
mvn spring-boot:run
```

---

## Project Structure

```
src/main/java/org/avi1606/financedataprocessing/
├── config/
│   ├── SecurityConfig.java         ✅ JWT + Spring Security
│   └── SwaggerConfig.java          ✅ API Documentation
├── controller/
│   ├── AuthController.java         ✅ Login & Register
│   ├── UserController.java         ✅ User Management
│   ├── TransactionController.java  ✅ Transaction CRUD
│   └── DashboardController.java    ✅ Analytics
├── service/
│   ├── AuthService.java            ✅ Authentication
│   ├── UserService.java            ✅ User Business Logic
│   ├── TransactionService.java     ✅ Transaction Business Logic
│   └── DashboardService.java       ✅ Analytics
├── repository/
│   ├── UserRepository.java         ✅ User Queries
│   ├── TransactionRepository.java  ✅ Transaction Queries
│   └── DashboardRepository.java    ✅ Dashboard Queries
├── model/
│   ├── User.java                   ✅ User Entity
│   └── Transaction.java            ✅ Transaction Entity
├── dto/
│   ├── LoginRequest.java           ✅ Login DTO
│   ├── RegisterRequest.java        ✅ Register DTO
│   ├── TransactionRequest.java     ✅ Transaction DTO
│   ├── TransactionResponse.java    ✅ Transaction Response
│   └── ... (more DTOs)
├── enums/
│   ├── Role.java                   ✅ VIEWER, ANALYST, ADMIN
│   ├── TransactionType.java        ✅ INCOME, EXPENSE
│   ├── Category.java               ✅ SALARY, FOOD, RENT, etc.
│   └── UserStatus.java             ✅ ACTIVE, INACTIVE
├── exception/
│   ├── GlobalExceptionHandler.java ✅ Error Handling
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
├── security/
│   ├── JwtUtil.java                ✅ JWT Token Generation & Validation
│   ├── JwtFilter.java              ✅ JWT Request Filter
│   └── UserDetailsServiceImpl.java  ✅ User Details Service
└── FinanceDataProcessingAndAccessControlBackendApplication.java ✅ Main Entry Point
```

---

## Technologies Used

- **Java 17** - Latest LTS version
- **Spring Boot 4.0.5** - Latest version
- **Spring Security** - JWT + Role-Based Access Control
- **Spring Data JPA** - ORM with Hibernate
- **H2 Database** - In-memory for development (PostgreSQL ready)
- **JJWT 0.12.3** - JWT Token handling
- **Springdoc OpenAPI** - Swagger/OpenAPI 3.0 documentation
- **Lombok** - Boilerplate reduction
- **Maven** - Build and dependency management

---

## What's Compiled & Ready

✅ All 36 Java source files
✅ Spring Security with JWT
✅ Role-based access control
✅ All API controllers
✅ Database repositories
✅ Business logic services
✅ Error handling
✅ Swagger documentation

---

## Next Steps

1. **Run the application**: `mvn spring-boot:run`
2. **Access Swagger UI**: http://localhost:8080/swagger-ui.html
3. **Test endpoints** using Swagger or cURL
4. **Monitor logs** for any runtime issues
5. **Use H2 Console** to inspect database: http://localhost:8080/h2-console

---

## Support

If you encounter any issues:
1. Check the `Troubleshooting` section above
2. Review the Spring Boot logs in the console
3. Verify all dependencies are downloaded (`mvn install`)
4. Ensure port 8080 is not in use
5. Check `application.properties` configuration

Good luck! 🚀

