# Stage 1: byg jar-filen
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Hent dependencies først, så de caches og kun hentes igen når pom.xml ændres
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Kopier koden og byg uden tests (testene kører i GitHub Actions)
COPY src ./src
RUN mvn -B package -DskipTests

# Stage 2: kør appen med kun en JRE
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
