# Pilot Quiz Platform

A comprehensive quiz application for pilots preparing for rating exams (Private, Instrument, Commercial, ATP).

## Architecture

- **Backend**: Spring Boot microservices with Spring Cloud
- **Frontend**: Single-SPA with React and Angular micro-frontends
- **Database**: PostgreSQL (database-per-service pattern)
- **Messaging**: Apache Kafka for event-driven communication
- **Infrastructure**: Docker, Jenkins CI/CD, ELK Stack

## Quick Start

### Prerequisites

- Java 17+
- Node.js 18+
- Docker & Docker Compose
- Maven 3.8+

### Local Development

```bash
# Start infrastructure (PostgreSQL, Kafka, etc.)
docker-compose -f infrastructure/docker-compose.dev.yml up -d

# Start backend services
cd backend/eureka-server && mvn spring-boot:run &
cd backend/api-gateway && mvn spring-boot:run &
cd backend/user-service && mvn spring-boot:run &
cd backend/quiz-service && mvn spring-boot:run &
cd backend/progress-service && mvn spring-boot:run &

# Start frontend
cd frontend/root-config && npm start
```

### Full Stack (Docker)

```bash
docker-compose -f infrastructure/docker-compose.yml up -d
```

Access the application at `http://localhost:9000`

## Project Structure

```
├── frontend/           # Micro-frontend applications
├── backend/            # Spring Boot microservices
├── infrastructure/     # Docker and deployment configs
├── jenkinsfiles/       # CI/CD pipeline definitions
└── docs/               # Project documentation
```

## Documentation

- [Architecture](docs/Architecture.md)
- [API Documentation](docs/API.md)
- [User Stories](docs/UserStories.md)
- [ERD](docs/ERD.md)

## Technology Stack

| Layer | Technology |
|-------|------------|
| Frontend Shell | Single-SPA |
| React MFE | React 18, Vite |
| Angular MFE | Angular 21 |
| API Gateway | Spring Cloud Gateway |
| Services | Spring Boot 3.2 |
| Database | PostgreSQL 15 |
| Messaging | Apache Kafka |
| CI/CD | Jenkins |
| Monitoring | ELK Stack |

---

*Generated with assistance from Gemini AI*
*Reviewed and modified by Richard Hawkins*
