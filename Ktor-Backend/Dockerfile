FROM gradle:8.10.2-jdk11 AS builder
WORKDIR /app

# Copy necessary files
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .
COPY src/ src/

# Set environment variable to exclude Android
ENV DEPLOYING_ON_RAILWAY=true

# Build the project
RUN sed -i 's/\r$//' gradlew && \
    chmod +x ./gradlew && \
    ./gradlew --no-daemon installDist -Dorg.gradle.java.home=/opt/java/openjdk


# Runtime image
FROM openjdk:11-jre-slim
WORKDIR /app

# Copy built distribution
COPY --from=builder /app/build/install/Ktor-Backend /app/

EXPOSE 8080
CMD ["./bin/Ktor-Backend"]