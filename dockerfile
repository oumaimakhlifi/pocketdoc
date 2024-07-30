# Étape 1 : Construction de l'application avec Maven
FROM maven:3.8.5-openjdk-17 AS builder

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier de configuration Maven
COPY pom.xml .

# Télécharger les dépendances Maven
RUN mvn dependency:go-offline

# Copier le code source de l'application
COPY src ./src

# Construire l'application
RUN mvn clean package -DskipTests

# Étape 2 : Création de l'image finale avec OpenJDK
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR construit depuis l'étape précédente
COPY --from=builder /app/target/pocketdoc-back.jar /app/pocketdoc-back.jar

# Exposer le port sur lequel l'application Spring Boot écoute
EXPOSE 8080

# Commande pour exécuter l'application Spring Boot
CMD ["java", "-jar", "/app/pocketdoc-back.jar"]
