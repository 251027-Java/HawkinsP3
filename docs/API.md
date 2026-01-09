# API Documentation

## Overview

All APIs are accessible through the **API Gateway** at `http://localhost:8888`.

For interactive API documentation, each service exposes **Swagger UI**:

- User Service: `http://localhost:8001/swagger-ui.html`
- Quiz Service: `http://localhost:8002/swagger-ui.html`
- Progress Service: `http://localhost:8003/swagger-ui.html`

---

## Authentication

All protected endpoints require a JWT Bearer token:

```
Authorization: Bearer <token>
```

Tokens are obtained from `/api/v1/auth/login` endpoint.

---

## User Service API (v1)

### Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/auth/register` | Create new user | Public |
| POST | `/api/v1/auth/login` | Login and get token | Public |

### Users

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/users/profile` | Get current user profile | User |
| PUT | `/api/v1/users/profile` | Update current profile | User |
| GET | `/api/v1/users/{id}` | Get user by ID | Admin |

---

## Quiz Service API (v1)

### Questions

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/questions` | List questions (with filters) | User |
| GET | `/api/v1/questions/{id}` | Get question details | User |
| POST | `/api/v1/questions` | Create question | Admin |
| PUT | `/api/v1/questions/{id}` | Update question | Admin |
| DELETE | `/api/v1/questions/{id}` | Delete question | Admin |
| POST | `/api/v1/questions/bulk` | Bulk upload from CSV | Admin |
| GET | `/api/v1/questions/template` | Download CSV template | Admin |

### Quizzes

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/quizzes` | List available quizzes | User |
| GET | `/api/v1/quizzes/{id}` | Get quiz with questions | User |
| POST | `/api/v1/quizzes` | Create quiz | Admin |
| PUT | `/api/v1/quizzes/{id}` | Update quiz | Admin |
| DELETE | `/api/v1/quizzes/{id}` | Delete quiz | Admin |

### Categories

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/categories` | List all categories | User |
| POST | `/api/v1/categories` | Create category | Admin |

---

## Progress Service API (v1)

### Attempts

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/attempts` | Submit quiz attempt | User |
| GET | `/api/v1/attempts/user/{userId}` | Get user's attempts | User |
| GET | `/api/v1/attempts/{id}` | Get attempt details | User |

### Progress

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/progress/user/{userId}` | Get progress by category | User |

### Analytics

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/analytics/weak-areas/{userId}` | Get weak areas analysis | User |

### Achievements

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/achievements` | List all achievements | User |
| GET | `/api/v1/achievements/user/{userId}` | Get user's achievements | User |

---

## Error Response Format

All errors follow a consistent format:

```json
{
  "timestamp": "2026-01-09T08:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message"
}
```

## HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request (validation error) |
| 401 | Unauthorized (invalid/missing token) |
| 403 | Forbidden (insufficient role) |
| 404 | Not Found |
| 409 | Conflict (e.g., duplicate email) |
| 500 | Internal Server Error |

---

*Generated with assistance from Gemini AI*
*Reviewed and modified by Richard Hawkins*
