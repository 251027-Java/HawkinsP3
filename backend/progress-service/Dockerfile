# Generated with assistance from Gemini AI
# Reviewed and modified by Richard Hawkins

# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8003

# Environment variables with defaults
ENV POSTGRES_HOST=postgres-progress
ENV POSTGRES_USER=pilotquiz
ENV POSTGRES_PASSWORD=pilotquiz123
ENV KAFKA_BOOTSTRAP_SERVERS=kafka:9092

ENTRYPOINT ["java", "-jar", "app.jar"]
