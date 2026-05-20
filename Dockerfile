# Build stage
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Environment variables with defaults
ENV SPRING_PROFILES_ACTIVE=prod
ENV SPRING_DATASOURCE_URL="jdbc:mysql://sakura-saki-sakura-saki-8f40.h.aivencloud.com:26530/defaultdb?sslMode=REQUIRED"
ENV SPRING_DATASOURCE_USERNAME="avnadmin"
ENV SPRING_DATASOURCE_PASSWORD="AVNS_dDB2Ptw4NTfc_MUy5CZ"

ENTRYPOINT ["java", "-jar", "app.jar"]
