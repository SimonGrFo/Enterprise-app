# Dockerfile
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Spring Boot JAR file from the host into the container
COPY target/enterprise-app.jar app.jar  # Adjust 'enterprise-app.jar' if needed

# Expose the application port (matches your .env PORT variable)
EXPOSE 8080

# Define the entry point for the container to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]