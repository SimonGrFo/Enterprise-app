FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY build/libs/enterprise-app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]