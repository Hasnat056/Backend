FROM gradle:7.3.3-jdk11 AS builder
WORKDIR /app

# Copy only Ktor backend files
COPY gradle/ /app/gradle/
COPY gradlew /app/
COPY Ktor-Backend/build.gradle.kts /app/Ktor-Backend/
COPY Ktor-Backend/src/ /app/Ktor-Backend/src/
COPY settings.gradle.kts /app/
COPY gradle.properties /app/

# Set environment variable to exclude Android
ENV DEPLOYING_ON_RAILWAY=true

# Build only Ktor backend
RUN chmod +x /app/gradlew && \
    cd /app/Ktor-Backend && \
    ./../gradlew :Ktor-Backend:installDist --no-daemon

# Runtime image
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/Ktor-Backend/build/install/Ktor-Backend /app/
EXPOSE 8080
CMD ["./bin/Ktor-Backend"]