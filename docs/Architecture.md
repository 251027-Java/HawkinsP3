# Pilot Quiz Platform - Architecture

## System Overview

The Pilot Quiz Platform is an enterprise-grade application for pilots preparing for FAA rating exams. It follows a microservice backend with micro-frontend architecture.

---

## Architecture Diagram

```mermaid
graph TB
    subgraph "Frontend - Single-SPA"
        RC[Root Config<br/>Port 9000]
        MFE_R[React MFE<br/>Auth & Dashboard<br/>Port 8080]
        MFE_A[Angular MFE<br/>Quiz & Progress<br/>Port 8081]
    end
    
    subgraph "Backend - Spring Cloud"
        GW[API Gateway<br/>Port 8888]
        EU[Eureka Server<br/>Port 8761]
        US[User Service<br/>Port 8001]
        QS[Quiz Service<br/>Port 8002]
        PS[Progress Service<br/>Port 8003]
    end
    
    subgraph "Infrastructure"
        KF[Kafka<br/>Port 9092]
        PG1[(User DB<br/>:5432)]
        PG2[(Quiz DB<br/>:5433)]
        PG3[(Progress DB<br/>:5434)]
    end
    
    RC --> MFE_R
    RC --> MFE_A
    MFE_R --> GW
    MFE_A --> GW
    GW --> EU
    GW --> US
    GW --> QS
    GW --> PS
    US --> PG1
    QS --> PG2
    PS --> PG3
    US --> KF
    QS --> KF
    PS --> KF
```

---

## Service Descriptions

| Service | Port | Purpose |
|---------|------|---------|
| **Eureka Server** | 8761 | Service discovery and registration |
| **API Gateway** | 8888 | Request routing, JWT validation, CORS |
| **User Service** | 8001 | Authentication, user profiles |
| **Quiz Service** | 8002 | Questions, quizzes, categories |
| **Progress Service** | 8003 | Attempts, scores, analytics |

---

## Technology Stack

| Layer | Technology | Version |
|-------|------------|---------|
| Frontend Shell | Single-SPA | 5.x |
| React MFE | React + Vite | 18.x |
| Angular MFE | Angular | 17.x |
| API Gateway | Spring Cloud Gateway | 2023.0.0 |
| Microservices | Spring Boot | 3.2.1 |
| Database | PostgreSQL | 15 |
| Messaging | Apache Kafka | 7.5.0 |
| CI/CD | Jenkins | Latest |
| Monitoring | ELK Stack | 8.x |

---

## Security Architecture

```mermaid
sequenceDiagram
    participant C as Client (MFE)
    participant G as API Gateway
    participant U as User Service
    participant S as Other Services

    C->>G: POST /api/v1/auth/login
    G->>U: Forward request
    U-->>G: JWT Token
    G-->>C: JWT Token
    
    C->>G: GET /api/v1/quizzes (Bearer Token)
    G->>G: Validate JWT
    G->>S: Forward + X-User-Id header
    S-->>G: Response
    G-->>C: Response
```

**Key Security Features:**

- JWT-based stateless authentication
- Token validation at API Gateway
- Role-based access control (ROLE_USER, ROLE_ADMIN)
- CORS configured for MFE origins

---

## Event-Driven Communication

```mermaid
flowchart LR
    US[User Service] -->|user.registered| K[Kafka]
    US -->|user.profile.updated| K
    QS[Quiz Service] -->|quiz.completed| K
    K -->|user.registered| PS[Progress Service]
    K -->|quiz.completed| PS
```

**Kafka Topics:**

- `user.registered` - Triggers progress initialization
- `user.profile.updated` - Syncs profile changes
- `quiz.completed` - Updates progress tracking

---

## Deployment Architecture

```mermaid
graph TB
    subgraph "EC2 #1 - Application Server"
        APP[Docker Compose Stack]
        APP --> FE[Frontend MFEs]
        APP --> BE[Backend Services]
        APP --> DB[(PostgreSQL)]
        APP --> KF[Kafka]
    end
    
    subgraph "EC2 #2 - Build & Monitoring"
        JK[Jenkins]
        ELK[ELK Stack]
    end
    
    JK -->|Deploy| APP
    BE -->|Logs| ELK
```

---

*Generated with assistance from Gemini AI*
*Reviewed and modified by Richard Hawkins*
