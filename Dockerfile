# Use an official Gradle image to build the project
FROM gradle:7.3.3-jdk11 AS builder
WORKDIR /app

# Copy project files into the container
COPY . .

# Build the project using Gradle
RUN gradle installDist

# Use a lightweight JDK image to run the application
FROM openjdk:11-jre-slim
WORKDIR /app

# Copy the built application from the builder stage
COPY --from=builder /app/build/install/Ktor-Backend /app/

# Expose the port your Ktor server listens on
EXPOSE 8080

# Run the Ktor server
CMD ["./bin/Ktor-Backend"]