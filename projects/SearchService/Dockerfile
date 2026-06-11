FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace
COPY . .

RUN chmod +x mvnw && ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

RUN addgroup -S authgroup && adduser -S authuser -G authgroup
USER authuser

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
