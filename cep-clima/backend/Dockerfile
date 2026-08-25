FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY backend/pom.xml ./
RUN mvn -q -B dependency:go-offline

COPY backend/src src

RUN mvn -q -B package

FROM eclipse-temurin:17-jre
WORKDIR /app

# curl e necessario para o healthcheck definido em docker-compose.yaml
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/api-cep-clima-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
