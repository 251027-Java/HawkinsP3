# Runbook

Common operations and troubleshooting for the Pilot Quiz Platform.

---

## Local Development

### Start Infrastructure

```bash
# Start PostgreSQL + Kafka
docker-compose -f infrastructure/docker-compose.dev.yml up -d

# Verify containers are running
docker ps
```

### Start Backend Services

Start services in this order:

```bash
# 1. Eureka Server (wait for it to be ready)
cd backend/eureka-server
mvn spring-boot:run
# Verify at http://localhost:8761

# 2. API Gateway
cd backend/api-gateway
mvn spring-boot:run

# 3. User Service
cd backend/user-service
mvn spring-boot:run
# Swagger at http://localhost:8001/swagger-ui.html
```

### Stop Infrastructure

```bash
docker-compose -f infrastructure/docker-compose.dev.yml down

# To also remove volumes:
docker-compose -f infrastructure/docker-compose.dev.yml down -v
```

---

## Health Checks

| Service | URL | Expected |
|---------|-----|----------|
| Eureka | <http://localhost:8761> | Dashboard |
| Gateway | <http://localhost:8888/actuator/health> | `{"status":"UP"}` |
| User Service | <http://localhost:8001/actuator/health> | `{"status":"UP"}` |

---

## Common Issues

### Eureka Connection Refused

**Symptom:** Services fail to register with Eureka  
**Cause:** Eureka Server not running  
**Solution:** Start Eureka Server first, wait 30 seconds before starting other services

### Database Connection Failed

**Symptom:** `Connection refused` to PostgreSQL  
**Cause:** Docker containers not running  
**Solution:**

```bash
docker-compose -f infrastructure/docker-compose.dev.yml up -d
```

### JWT Token Invalid

**Symptom:** 401 Unauthorized on protected endpoints  
**Cause:** Token expired or wrong secret  
**Solution:** Get new token from `/api/v1/auth/login`

### Port Already in Use

**Symptom:** `Address already in use`  
**Solution:** Find and kill the process:

```bash
# Windows
netstat -ano | findstr :8761
taskkill /PID <pid> /F
```

---

## API Testing Examples

### Register User

```bash
curl -X POST http://localhost:8888/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "pilot@test.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "Pilot"
  }'
```

### Login

```bash
curl -X POST http://localhost:8888/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "pilot@test.com",
    "password": "password123"
  }'
```

### Get Profile (with token)

```bash
curl http://localhost:8888/api/v1/users/profile \
  -H "Authorization: Bearer <token>"
```

---

*Generated with assistance from Gemini AI*
*Reviewed and modified by Richard Hawkins*
