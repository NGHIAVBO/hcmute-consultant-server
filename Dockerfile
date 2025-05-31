FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/HcmuteConsultantServer.jar app.jar

# Cài đặt gói procps để sử dụng lệnh free
RUN apt-get update && apt-get install -y procps

# Thiết lập các biến môi trường cần thiết
ENV SERVER_PORT=8080
ENV RAILWAY_ENV=true

# Thêm health check để Railway biết khi nào ứng dụng đã sẵn sàng
HEALTHCHECK --interval=30s --timeout=30s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/health || exit 1

# Tăng thời gian chờ khởi động
EXPOSE 8080
CMD ["sh", "-c", "echo '==== MEMORY INFO ===='; free -h || cat /proc/meminfo; echo '===================='; java -Xmx384m -jar -Dspring.datasource.hikari.initialization-fail-timeout=60000 app.jar"]