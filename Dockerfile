# ----------- STAGE 1: Build ------------
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ----------- STAGE 2: Run ------------
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/Finance-Manager-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
