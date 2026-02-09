# Multi-stage build for optimized Docker image
# Stage 1: Build stage
# Note: Using standard JDK image instead of alpine for better ARM64 (Apple Silicon) compatibility
FROM gradle:8.14.4-jdk17 AS builder

WORKDIR /app

# Copy Gradle files for dependency caching
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN gradle bootJar --no-daemon -x test

# Stage 2: Runtime stage
# Note: Using standard JRE image instead of alpine for better ARM64 (Apple Silicon) compatibility
FROM eclipse-temurin:17-jre

WORKDIR /app

# Create non-root user for security
# Note: Using groupadd/useradd instead of addgroup/adduser for Debian-based images
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]