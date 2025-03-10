FROM gradle:7.3.3-jdk11 AS builder
WORKDIR /app

# Copy only necessary files
COPY gradle/ /app/gradle/
COPY gradlew /app/
COPY Ktor-Backend/build.gradle.kts /app/Ktor-Backend/
COPY Ktor-Backend/src/ /app/Ktor-Backend/src/
COPY settings.gradle.kts /app/
COPY gradle.properties /app/

# Set execute permissions
RUN chmod +x /app/gradlew

# Build with Railway-compliant cache
WORKDIR /app/Ktor-Backend
RUN --mount=type=cache,id=gradle-cache-$RAILWAY_PROJECT_ID,target=/root/.gradle \
    ./../gradlew :Ktor-Backend:installDist --no-daemon

# Runtime image
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/Ktor-Backend/build/install/Ktor-Backend /app/
EXPOSE 8080
CMD ["./bin/Ktor-Backend"]