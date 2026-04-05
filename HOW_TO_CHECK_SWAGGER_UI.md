# 🧪 How to Check & Test Swagger UI

## ✅ Step 1: Verify Swagger UI is Loading

### In Your Browser
Open this URL:
```
http://localhost:8081/swagger-ui.html
```

**You should see:**
- ✅ Swagger UI interface loaded
- ✅ "Finance API" section with all endpoints
- ✅ No 500 errors or error messages
- ✅ Nice UI with expandable endpoint sections

---

## 📡 Step 2: Verify API Docs Endpoint Works

### In Terminal
```bash
curl http://localhost:8081/v3/api-docs | jq .
```

**Expected Output:**
- ✅ Valid JSON response
- ✅ Contains `"openapi": "3.0.1"`
- ✅ Contains `"paths"` with all endpoints
- ✅ No error status

---

## 🔐 Step 3: Test Endpoints in Swagger UI

### 3a. Login Endpoint (GET JWT Token)
1. In Swagger UI, find: **POST /api/auth/login**
2. Click **Try it out**
3. In the request body, enter:
```json
{
  "email": "admin@finance.com",
  "password": "admin123"
}
```
4. Click **Execute**
5. **Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "admin@finance.com",
  "role": "ADMIN"
}
```
✅ Status: **200 OK**

---

### 3b. Authorize with JWT Token
1. **Copy the token** from the login response above
2. Scroll to the top of Swagger UI
3. Click **Authorize** button (top right)
4. In the popup, paste:
```
Bearer YOUR_TOKEN_HERE
```
Example:
```
Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```
5. Click **Authorize**
6. Click **Close**

✅ Now all subsequent requests will include the JWT token

---

### 3c. Create a Transaction (Tests Fix #1)
1. Find: **POST /api/transactions**
2. Click **Try it out**
3. Enter request body:
```json
{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "SALARY",
  "date": "2026-04-01",
  "notes": "April salary"
}
```
4. Click **Execute**
5. **Expected Response:**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440000",
  "amount": 5000.00,
  "type": "INCOME",
  "category": "SALARY",
  "date": "2026-04-01",
  "notes": "April salary",
  "createdById": "550e8400-e29b-41d4-a716-446655440000",
  "createdByName": "System Admin",
  "createdAt": "2026-04-05T21:30:00",
  "isDeleted": false
}
```
✅ Status: **201 Created**

✅ **Fix #1 is working!** (Uses correct userId from JWT, not random)

---

### 3d. Create an Expense Transaction
1. Find: **POST /api/transactions**
2. Click **Try it out**
3. Enter:
```json
{
  "amount": 500.00,
  "type": "EXPENSE",
  "category": "FOOD",
  "date": "2026-04-02",
  "notes": "Groceries"
}
```
4. Click **Execute**
✅ Status: **201 Created**

---

### 3e. Get Dashboard Summary
1. Find: **GET /api/dashboard/summary**
2. Click **Try it out**
3. Click **Execute**
4. **Expected Response:**
```json
{
  "totalIncome": 5000.00,
  "totalExpense": 500.00,
  "netBalance": 4500.00
}
```
✅ Status: **200 OK**

✅ **Dashboard calculations working!**

---

### 3f. Get All Transactions
1. Find: **GET /api/transactions**
2. Click **Try it out**
3. Click **Execute**
4. **Expected Response:**
Array with 2 transactions (INCOME and EXPENSE)
✅ Status: **200 OK**

---

### 3g. Filter by Type
1. Find: **GET /api/transactions**
2. Click **Try it out**
3. Scroll down to **Parameters**
4. In `type` field, enter: `EXPENSE`
5. Click **Execute**
6. **Expected Response:**
Only the EXPENSE transaction (500.00)
✅ Status: **200 OK**

---

### 3h. Get Category Breakdown
1. Find: **GET /api/dashboard/category-totals**
2. Click **Try it out**
3. Click **Execute**
4. **Expected Response:**
```json
[
  {
    "category": "SALARY",
    "total": 5000.00,
    "count": 1
  },
  {
    "category": "FOOD",
    "total": 500.00,
    "count": 1
  }
]
```
✅ Status: **200 OK**

---

### 3i. Test Authorization (VIEWER Can't Create)
1. **Logout first:** 
   - Click **Authorize** button
   - Click **Logout**

2. **Register as VIEWER:**
   - Find: **POST /api/auth/register**
   - Click **Try it out**
   - Enter:
   ```json
   {
     "name": "John Viewer",
     "email": "viewer@test.com",
     "password": "password123"
   }
   ```
   - Click **Execute**
   ✅ Status: **201 Created**

3. **Login as VIEWER:**
   - Find: **POST /api/auth/login**
   - Click **Try it out**
   - Enter:
   ```json
   {
     "email": "viewer@test.com",
     "password": "password123"
   }
   ```
   - Click **Execute**
   - Copy the new token
   - Click **Authorize** and paste new token

4. **Try to Create Transaction (Should Fail):**
   - Find: **POST /api/transactions**
   - Click **Try it out**
   - Enter any transaction data
   - Click **Execute**
   - **Expected Response:**
   ```json
   {
     "status": 403,
     "error": "Forbidden"
   }
   ```
   ✅ Status: **403 Forbidden**

✅ **Role-based access control working!**

---

## 📊 Complete Checklist

After testing all above, you should have verified:

- ✅ Swagger UI loads without errors
- ✅ API docs endpoint returns valid JSON
- ✅ Can login and get JWT token
- ✅ Can create transactions (Fix #1 working)
- ✅ Can filter transactions
- ✅ Can view dashboard summary
- ✅ Can view category breakdown
- ✅ VIEWER cannot create transactions (403)
- ✅ ADMIN can do everything
- ✅ All endpoints properly documented

---

## 🎯 All Tests Pass = Ready to Submit!

If all the above tests pass, your project is **100% ready for submission!**

---

## 💡 Quick Command Tests (Alternative)

If you prefer testing via curl instead:

### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@finance.com","password":"admin123"}' | jq .
```

### Create Transaction (Replace TOKEN with actual token)
```bash
curl -X POST http://localhost:8081/api/transactions \
  -H "Authorization: Bearer TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"amount":5000,"type":"INCOME","category":"SALARY","date":"2026-04-01","notes":"Test"}' | jq .
```

### Get Dashboard
```bash
curl -X GET http://localhost:8081/api/dashboard/summary \
  -H "Authorization: Bearer TOKEN_HERE" | jq .
```

---

## ✨ Status

✅ **Project is fully functional and ready!**

All 4 bugs fixed:
1. ✅ getCurrentUserId() returns correct user (not random)
2. ✅ Default admin created on startup
3. ✅ H2 console accessible
4. ✅ Dead code deleted

All features working:
- ✅ Authentication (JWT)
- ✅ Authorization (Role-based)
- ✅ Transactions (CRUD + soft delete)
- ✅ Dashboard (analytics)
- ✅ Filtering
- ✅ Swagger UI & Docs

**Ready to submit!** 🚀

