FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src/ ./src
RUN mvn clean package

FROM openjdk:21-slim-buster
WORKDIR /app
COPY --from=build /app/target/AuthService-1.0-SNAPSHOT.jar AuthService.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "AuthService.jar"]