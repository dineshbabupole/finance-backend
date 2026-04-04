Here is the complete README in one single block — just copy and paste the whole thing into your README.md file:
markdown# Finance Backend

This is a backend application I built for managing financial records.
Different users have different roles and each role has different level of access.
For example an admin can add transactions but a viewer can only see the summary.

## What I Used

- Java 17
- Spring Boot 3.5
- SQLite database
- JWT for authentication
- Maven for build

## How to Run This Project

Make sure you have Java 17 and Maven installed.

Clone the project:
```bash
git clone https://github.com/yourusername/finance-backend.git
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
API documentation:
```
http://localhost:8080/swagger-ui/index.html
```

## Default Admin Login

When the app starts it automatically creates one admin account.

| | |
|---|---|
| Email | admin@zorvyn.com |
| Password | admin123 |

Login with this and use the token to promote other users.

## Roles

I defined three roles in this system based on what each person needs to do.

**VIEWER** — can only see transactions and dashboard summary. Cannot modify anything.

**ANALYST** — everything a viewer can do plus access to category breakdown and monthly trends for analysis.

**ADMIN** — full access. Can create, update, delete transactions and manage users.

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
| GET | /api/transactions/{id} | All | Get one transaction |
| GET | /api/transactions/filter/type?type=INCOME | All | Filter by type |
| GET | /api/transactions/filter/category?category=Salary | All | Filter by category |
| GET | /api/transactions/filter/date?start=2026-03-01 | All | Filter by date |
| POST | /api/transactions | Admin | Add new transaction |
| PUT | /api/transactions/{id} | Admin | Edit transaction |
| DELETE | /api/transactions/{id} | Admin | Soft delete transaction |

### Dashboard
| Method | URL | Who | What it does |
|---|---|---|---|
| GET | /api/dashboard/summary | All | Income, expenses, balance |
| GET | /api/dashboard/by-category | Analyst, Admin | Spending per category |
| GET | /api/dashboard/trends | Analyst, Admin | Month by month breakdown |

### User Management
| Method | URL | Who | What it does |
|---|---|---|---|
| GET | /api/users | Admin | List all users |
| GET | /api/users/{id} | Admin | Get one user |
| PUT | /api/users/{id}/role | Admin | Change user role |
| PUT | /api/users/{id}/activate | Admin | Activate user |
| PUT | /api/users/{id}/deactivate | Admin | Deactivate user |

## Database Tables

### users
| Column | Type | Notes |
|---|---|---|
| id | Long | auto generated |
| name | String | full name |
| email | String | must be unique |
| password | String | stored as BCrypt hash |
| role | Enum | VIEWER, ANALYST, ADMIN |
| active | Boolean | false means blocked |

### transactions
| Column | Type | Notes |
|---|---|---|
| id | Long | auto generated |
| amount | BigDecimal | used BigDecimal not double to avoid money precision issues |
| type | Enum | INCOME or EXPENSE |
| category | String | example Salary, Marketing |
| date | LocalDate | transaction date |
| notes | String | optional description |
| deleted | Boolean | true means soft deleted |

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

## Decisions I Made

**Why soft delete?**
Financial records should never be permanently removed. Soft delete marks
them as deleted but keeps the data safe in case it needs to be recovered.

**Why BigDecimal for amount?**
Double has floating point precision issues with money calculations.
For example 0.1 + 0.2 gives 0.30000000000000004 in double.
BigDecimal avoids this completely.

**Why VIEWER as default role?**
New users should get the least access by default.
Admin can always promote them later. This follows the least privilege principle.

**Why JWT?**
JWT is stateless so no session needs to be stored on the server.
Every request carries the token and the role is embedded inside it.

**Why SQLite?**
Simple to set up with no external installation needed.
The database file is created automatically when the app starts.
Can be swapped to PostgreSQL for production with minimal changes.

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
  "name": "Super Admin"
}
```

### Create Transaction
```json
POST /api/transactions
Authorization: Bearer 
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

### Error Example
```json
{
  "status": 403,
  "error": "Access Denied",
  "message": "You do not have permission to perform this action",
  "timestamp": "2026-04-04T10:00:00"
}
```