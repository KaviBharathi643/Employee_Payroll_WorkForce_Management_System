# Multi-stage Docker build for Spring Boot Backend
# Stage 1: Build the application
FROM maven:3.9.8-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

COPY src ./src

ENV MAVEN_OPTS="-Xms128m -Xmx384m"

RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre

RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]