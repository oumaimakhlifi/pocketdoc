# Étape 1: Construire le JAR avec Maven
FROM maven:3.8.6-openjdk-17 AS build

WORKDIR /app

COPY pocketdoc-back/pom.xml ./
RUN mvn dependency:go-offline

COPY pocketdoc-back/src ./src
RUN mvn package -DskipTests

# Étape 2: Exécuter l'application Spring Boot
FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/pocketdoc-back-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
