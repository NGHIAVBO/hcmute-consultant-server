FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar HcmuteConsultantServer.jar

ENV SERVER_PORT=8080

ENV RAILWAY_ENV=true



HEALTHCHECK --interval=30s --timeout=30s --start-period=60s --retries=3 \

  CMD curl -f http://localhost:8080/api/v1/health || exit 1

EXPOSE 8080

CMD ["java", "-jar", "-Dspring.datasource.hikari.initialization-fail-timeout=60000", "HcmuteConsultantServer.jar"]