# Architecture Decision Records

This document tracks significant architectural decisions made during the development of the Pilot Quiz Platform.

---

## ADR-001: Microservice Architecture

**Date:** 2026-01-08  
**Status:** Accepted  
**Context:** P3 requires enterprise-grade architecture with multiple services.  

**Decision:** Use microservice architecture with Spring Cloud for service orchestration.

**Rationale:**

- Meets P3 requirement for 3+ microservices
- Enables independent deployment
- Supports team scalability
- Industry-standard approach

**Consequences:**

- Increased operational complexity
- Need for service discovery (Eureka)
- Need for API Gateway
- Distributed data management

---

## ADR-002: Database Per Service

**Date:** 2026-01-08  
**Status:** Accepted  
**Context:** Each microservice needs data persistence.  

**Decision:** Each service has its own PostgreSQL database instance.

**Rationale:**

- Meets P3 database-per-service requirement
- Loose coupling between services
- Independent schema evolution
- Service autonomy

**Consequences:**

- Data consistency via eventual consistency (Kafka events)
- No cross-service JOINs
- Need for 3 PostgreSQL containers

---

## ADR-003: JWT for Authentication

**Date:** 2026-01-08  
**Status:** Accepted  
**Context:** Need stateless authentication across microservices.  

**Decision:** Use JWT tokens issued by User Service, validated at API Gateway.

**Rationale:**

- Stateless = horizontal scalability
- Standard industry practice
- Single point of validation (Gateway)
- User info passed via headers to services

**Consequences:**

- Token revocation requires additional work
- Secret key must be shared between User Service and Gateway
- Token expiration handling on frontend

---

## ADR-004: Single-SPA for Micro-Frontends

**Date:** 2026-01-08  
**Status:** Accepted  
**Context:** P3 requires micro-frontend architecture with React and Angular.  

**Decision:** Use Single-SPA as the orchestration framework.

**Rationale:**

- Framework-agnostic (supports both React and Angular)
- Established community and documentation
- Route-based MFE mounting
- Independent deployability

**Consequences:**

- Shared dependencies need careful management
- Root config becomes a critical component
- Cross-MFE communication via Custom Events

---

## ADR-005: Kafka for Event-Driven Communication

**Date:** 2026-01-08  
**Status:** Accepted  
**Context:** P3 requires at least one event-driven workflow.  

**Decision:** Use Apache Kafka for asynchronous inter-service communication.

**Rationale:**

- High throughput event streaming
- Durable message storage
- Decouples services
- Supports event sourcing patterns

**Use Cases:**

- `user.registered` → Initialize progress tracking
- `quiz.completed` → Update progress statistics

**Consequences:**

- Requires Zookeeper
- Added infrastructure complexity
- Need to handle eventual consistency

---

## ADR-006: Bulk Question Upload via CSV

**Date:** 2026-01-08  
**Status:** Accepted  
**Context:** User needs easy way to manage questions without database access.  

**Decision:** Implement CSV-based bulk upload with downloadable template.

**Rationale:**

- User-friendly for non-technical admins
- Easy to prepare in spreadsheet
- Batch processing efficiency
- Template ensures correct format

**API Design:**

- `GET /api/v1/questions/template` - Download template
- `POST /api/v1/questions/bulk` - Upload CSV

**Consequences:**

- Need CSV parsing library
- Validation error reporting
- Transaction handling for partial failures

---

## ADR-007: Dark Mode UI

**Date:** 2026-01-08  
**Status:** Accepted  
**Context:** User preference for clean, professional UI with dark mode.  

**Decision:** Implement dark mode with CSS custom properties and theme toggle.

**Rationale:**

- User requested feature
- Reduces eye strain
- Modern UI expectation
- CSS variables make theming easy

**Implementation:**

- Shared design tokens in utility module
- Theme stored in localStorage
- System preference detection

---

## ADR-008: AWS RDS for Production Databases

**Date:** 2026-01-09  
**Status:** Accepted  
**Context:** Production deployment requires managed, scalable databases.  

**Decision:** Use AWS RDS for PostgreSQL in production, local containers for development.

**Rationale:**

- Managed service reduces operational burden
- Automated backups and point-in-time recovery
- Multi-AZ deployment for high availability
- Automatic patching and maintenance
- Scalable storage and compute

**Environment Configuration:**

| Environment | Database Host | Configuration Source |
|-------------|---------------|---------------------|
| Development | `postgres-*` containers | `docker-compose.dev.yml` |
| Production | AWS RDS endpoints | Environment variables / AWS Secrets Manager |

**Environment Variables:**

```
SPRING_DATASOURCE_URL=jdbc:postgresql://<RDS_ENDPOINT>:5432/<DB_NAME>
SPRING_DATASOURCE_USERNAME=<from AWS Secrets Manager>
SPRING_DATASOURCE_PASSWORD=<from AWS Secrets Manager>
```

**Consequences:**

- Need to manage RDS credentials securely (AWS Secrets Manager)
- VPC configuration for RDS access from EC2
- Separate development and production configurations
- Cost considerations for RDS instances

---

*Generated with assistance from Gemini AI*
*Reviewed and modified by Richard Hawkins*
