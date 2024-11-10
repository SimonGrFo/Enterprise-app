# Dockerfile
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Spring Boot JAR file from the host into the container
COPY build/libs/enterprise-app-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port (matches your .env PORT variable)
EXPOSE 8080

# Define the entry point for the container to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]