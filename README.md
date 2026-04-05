# Finance Backend

This is a backend application I built for managing financial records.
Different users have different roles and each role has different level of access.
For example an admin can add transactions but a viewer can only see the summary.

I built this using Java Spring Boot with SQLite for local development and PostgreSQL for deployment.
JWT is used for authentication so every request carries the token and the role is embedded inside it.

---

## What I Used

- Java 17
- Spring Boot 3.5
- PostgreSQL (deployed) / SQLite (local)
- JWT for authentication
- Maven for build
- Swagger UI for API documentation

---

## How to Run This Project

Make sure you have Java 17 and Maven installed.

Clone the project:
```bash
git clone https://github.com/dineshbabupole/finance-backend.git
cd finance-backend
```

Run it:
```bash
mvn spring-boot:run
```

It will start on:
```
http://localhost:8080
```

Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```

---

## Live API

Base URL:
```
https://finance-backend-762n.onrender.com
```

Swagger UI:
```
https://finance-backend-762n.onrender.com/swagger-ui/index.html
```

> Note: The app is hosted on Render free tier. If it has been inactive
> it may take 30-60 seconds to wake up on first request. Please wait and try again.

---

## Test Credentials

Use these accounts to test the APIs directly on Swagger.

### Admin Account
| | |
|---|---|
| Email | admin@zorvyn.com |
| Password | admin123 |
| Access | Full access - create, update, delete transactions and manage users |

### Analyst Account
| | |
|---|---|
| Email | analyst@zorvyn.com |
| Password | analyst123 |
| Access | View transactions, category totals, monthly trends |

### Viewer Account
| | |
|---|---|
| Email | viewer@zorvyn.com |
| Password | viewer123 |
| Access | View transactions and dashboard summary only |

### How to Use on Swagger
1. Open Swagger UI link above
2. Call POST /api/auth/login with any above credentials
3. Copy the token from response
4. Click Authorize button on top right of Swagger page
5. Paste token as: Bearer your-token-here
6. Now test any endpoint

---

## Roles

I defined three roles in this system based on what each person needs to do.

**VIEWER** - can only see transactions and dashboard summary. Cannot modify anything.
This is like a company director who just wants to know if the company is profitable.

**ANALYST** - everything a viewer can do plus access to category breakdown and monthly trends.
This is like a finance analyst who studies the data to find patterns and insights.

**ADMIN** - full access. Can create, update, delete transactions and manage users.
This is like a finance manager who is responsible for entering and managing all records.

New users get VIEWER role by default because they should have least access until admin promotes them.

---

## Permissions Table

| Action | VIEWER | ANALYST | ADMIN |
|---|---|---|---|
| View transactions | ✅ | ✅ | ✅ |
| Filter transactions | ✅ | ✅ | ✅ |
| Create transaction | ❌ | ❌ | ✅ |
| Update transaction | ❌ | ❌ | ✅ |
| Delete transaction | ❌ | ❌ | ✅ |
| Dashboard summary | ✅ | ✅ | ✅ |
| Category totals | ❌ | ✅ | ✅ |
| Monthly trends | ❌ | ✅ | ✅ |
| Manage users | ❌ | ❌ | ✅ |

---

## APIs

### Authentication
| Method | URL | Who | What it does |
|---|---|---|---|
| POST | /api/auth/register | Public | Create new account |
| POST | /api/auth/login | Public | Login and get JWT token |

### Transactions
| Method | URL | Who | What it does |
|---|---|---|---|
| GET | /api/transactions | All | Get all transactions |
| GET | /api/transactions/{id} | All | Get one transaction by id |
| GET | /api/transactions/filter/type?type=INCOME | All | Filter by type |
| GET | /api/transactions/filter/category?category=Salary | All | Filter by category |
| GET | /api/transactions/filter/date?start=2026-03-01 | All | Filter by date range |
| POST | /api/transactions | Admin | Add new transaction |
| PUT | /api/transactions/{id} | Admin | Edit transaction |
| DELETE | /api/transactions/{id} | Admin | Soft delete transaction |

### Dashboard
| Method | URL | Who | What it does |
|---|---|---|---|
| GET | /api/dashboard/summary | All | Total income, expenses and net balance |
| GET | /api/dashboard/by-category | Analyst, Admin | How much spent per category |
| GET | /api/dashboard/trends | Analyst, Admin | Month by month income and expense breakdown |

### User Management
| Method | URL | Who | What it does |
|---|---|---|---|
| GET | /api/users | Admin | List all users |
| GET | /api/users/{id} | Admin | Get one user |
| PUT | /api/users/{id}/role | Admin | Change user role |
| PUT | /api/users/{id}/activate | Admin | Activate a deactivated user |
| PUT | /api/users/{id}/deactivate | Admin | Block a user from logging in |

---

## Database Tables

### users
| Column | Type | Notes |
|---|---|---|
| id | Long | auto generated |
| name | String | full name |
| email | String | must be unique |
| password | String | stored as BCrypt hash so plain text is never saved |
| role | Enum | VIEWER, ANALYST, ADMIN |
| active | Boolean | false means user is blocked from logging in |

### transactions
| Column | Type | Notes |
|---|---|---|
| id | Long | auto generated |
| amount | BigDecimal | used BigDecimal not double to avoid money precision issues |
| type | Enum | INCOME or EXPENSE |
| category | String | example Salary, Marketing, Server Costs |
| date | LocalDate | transaction date |
| notes | String | optional description |
| deleted | Boolean | true means soft deleted - data is never permanently removed |

---

## Folder Structure
```
com.dinesh.finance/
├── config/        → security config, swagger setup, data seeder
├── controller/    → all api endpoints
├── service/       → business logic lives here
├── repository/    → database queries
├── model/         → entity classes
├── dto/           → what api receives and sends back
├── security/      → jwt filter and token utility
└── exception/     → handles all errors in one place
```

---

## Decisions I Made

**Why soft delete?**
Financial records should never be permanently removed.
Soft delete marks them as deleted but keeps the data safe
in case it needs to be recovered later.

**Why BigDecimal for amount?**
Double has floating point precision issues with money calculations.
For example 0.1 + 0.2 gives 0.30000000000000004 in double.
BigDecimal avoids this completely.

**Why VIEWER as default role?**
New users should get the least access by default.
Admin can always promote them later.
This follows the least privilege principle - give minimum access first.

**Why JWT?**
JWT is stateless so no session needs to be stored on the server.
Every request carries the token and the role is embedded inside it.
This makes the system more scalable and clean.

**Why separate filter endpoints?**
Instead of combining all filters into one endpoint I created separate endpoints
for type, category and date range. Each endpoint has one clear purpose
which makes the API easier to understand and use.

**Why deactivate instead of delete users?**
If a user is deleted their transaction history becomes orphaned.
Deactivating blocks them from logging in while keeping their data intact.

---

## Example Requests

### Login
```json
POST /api/auth/login
{
  "email": "admin@zorvyn.com",
  "password": "admin123"
}
```
Response:
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "role": "ADMIN",
  "name": "Admin User"
}
```

### Create Transaction
```json
POST /api/transactions
Authorization: Bearer your-token-here

{
  "amount": 50000,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-03-01",
  "notes": "March salary from client"
}
```
Response:
```json
{
  "id": 1,
  "amount": 50000,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-03-01",
  "notes": "March salary from client"
}
```

### Dashboard Summary
```json
GET /api/dashboard/summary
Authorization: Bearer your-token-here
```
Response:
```json
{
  "totalIncome": 80000,
  "totalExpenses": 15000,
  "netBalance": 65000,
  "totalTransactions": 3
}
```

### Error Response Example
```json
{
  "status": 403,
  "error": "Access Denied",
  "message": "You do not have permission to perform this action",
  "timestamp": "2026-04-05T10:00:00"
}
```