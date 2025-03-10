# Use Gradle image to build the Ktor backend
FROM gradle:7.3.3-jdk11 AS builder
WORKDIR /app

# Copy minimum required files for Gradle
COPY gradle/ /app/gradle/
COPY gradlew /app/
COPY Ktor-Backend/build.gradle.kts /app/Ktor-Backend/
COPY Ktor-Backend/src/ /app/Ktor-Backend/src/
COPY settings.gradle.kts /app/

# If gradle.properties exists, copy it
COPY gradle.properties /app/

# Make gradlew executable
RUN chmod +x /app/gradlew

# Build the Ktor module with Railway-compliant cache IDs

WORKDIR /app/Ktor-Backend
RUN --mount=type=cache,target=/root/.gradle \
    rm -rf /root/.gradle && \
    DEPLOYING_ON_RAILWAY=true ./../gradlew :Ktor-Backend:installDist --no-daemon

# Runtime image
FROM openjdk:11-jre-slim
WORKDIR /app

# Copy only necessary runtime files
COPY --from=builder /app/Ktor-Backend/build/install/Ktor-Backend /app/

# Configure runtime environment
ENV JAVA_OPTS="-Xmx300m -Xss512k -Dfile.encoding=UTF-8"
EXPOSE 8080

# Health check and startup command
HEALTHCHECK --interval=30s --timeout=3s \
    CMD curl -f http://localhost:8080/health || exit 1

CMD ["./bin/Ktor-Backend"]