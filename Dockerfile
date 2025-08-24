# Multi-stage build for YouTube to Spotify Playlist Converter

# Stage 1: Build stage
FROM maven:3.9.4-eclipse-temurin-17 as build

WORKDIR /app

# Copy Maven files first for better layer caching
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Create a non-root user
RUN addgroup -g 1001 -S appuser && \
    adduser -S appuser -u 1001 -G appuser

# Copy the built JAR from the build stage
COPY --from=build /app/target/youtube-spotify-converter-*.jar app.jar

# Change ownership to appuser
RUN chown appuser:appuser app.jar

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UnlockExperimentalVMOptions -XX:+UseZGC"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Labels for metadata
LABEL maintainer="GitHub Copilot"
LABEL description="YouTube to Spotify Playlist Converter API"
LABEL version="1.0"
