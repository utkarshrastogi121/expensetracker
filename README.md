# 💰 ExpenseTracker Backend

A secure and scalable Expense Management REST API built with Spring Boot, featuring JWT authentication, Redis caching, PostgreSQL, analytics, budgeting, and AI-powered financial insights using Gemini AI.

## 🚀 Features

- 🔐 JWT Authentication & Authorization
- 💸 Expense Management
- 🎯 Budget Tracking
- 🏷️ Category Management
- 📊 Expense Analytics & Reports
- 🤖 Gemini AI Financial Insights
- ⚡ Redis Caching
- 📖 Swagger API Documentation
- 🐳 Docker Support

## 🛠️ Tech Stack

- Spring Boot
- Spring Security
- JWT
- PostgreSQL
- Spring Data JPA / Hibernate
- Redis
- Gemini AI API
- Swagger / OpenAPI
- Docker
- Maven

## ⚙️ Setup

### Clone Repository

```bash
git clone https://github.com/utkarshrastogi121/expensetracker.git
cd expensetracker
```

### Configure Environment Variables

```properties
DB_URL=
DB_USERNAME=
DB_PASSWORD=

JWT_SECRET=

REDIS_HOST=
REDIS_PORT=

GEMINI_API_KEY=
```

### Run Application

```bash
mvn spring-boot:run
```

Or with Docker:

```bash
docker-compose up -d
```

## 📖 API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## 🏗️ Modules

- Authentication
- Expenses
- Budgets
- Categories
- Analytics
- AI Insights

## 👨‍💻 Author

**Utkarsh Rastogi**

- GitHub: https://github.com/utkarshrastogi121

⭐ Star the repository if you found it useful!
