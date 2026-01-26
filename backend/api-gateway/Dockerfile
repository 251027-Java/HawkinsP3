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

EXPOSE 8888

# Environment variables with defaults
ENV EUREKA_URI=http://eureka-server:8761/eureka/
ENV JWT_SECRET=your-secret-key-change-in-production

ENTRYPOINT ["java", "-jar", "app.jar"]
