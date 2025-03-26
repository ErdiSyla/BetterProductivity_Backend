FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-slim-buster
WORKDIR /app
RUN adduser --system appuser
COPY --from=build /app/target/AuthService-1.0-SNAPSHOT.jar AuthService.jar
EXPOSE 8080 8091
USER appuser
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
CMD curl -f http://localhost:8091/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "AuthService.jar"]