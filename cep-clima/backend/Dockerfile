FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY backend/mvnw backend/pom.xml ./
COPY backend/.mvn .mvn
COPY backend/src src
COPY frontend/index.html src/main/resources/static/index.html
COPY frontend/esuda-logo.png src/main/resources/static/esuda-logo.png

RUN chmod +x mvnw && ./mvnw -q package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/api-cep-clima-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
