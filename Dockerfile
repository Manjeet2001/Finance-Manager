# ----------- STAGE 1: Build -----------
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY test.sh .

RUN chmod +x test.sh
RUN ./test.sh && mvn clean package

# ----------- STAGE 2: Run ------------
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]