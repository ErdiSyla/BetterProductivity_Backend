FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-slim-buster
WORKDIR /app
RUN adduser --system --uid 1000 appuser
COPY --from=build /app/target/User-1.0-SNAPSHOT.jar User.jar
EXPOSE 8080 8091
USER appuser
ENTRYPOINT ["java", "-jar", "User.jar"]