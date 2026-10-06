FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -B -q dependency:go-offline -Pproduction || true
COPY src src
# Production build: Vaadin bundles the frontend (downloads Node.js on its own)
RUN mvn -B -Pproduction package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /src/target/quarkus-app /app
EXPOSE 8080
CMD ["java", "-jar", "/app/quarkus-run.jar"]
