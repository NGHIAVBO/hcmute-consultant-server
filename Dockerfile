FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/HcmuteConsultantServer.jar HcmuteConsultantServer.jar

ENV SERVER_PORT=9090
ENV RAILWAY_ENV=true

EXPOSE 9090
CMD ["java", "-jar", "-Dspring.datasource.hikari.initialization-fail-timeout=60000", "HcmuteConsultantServer.jar"]