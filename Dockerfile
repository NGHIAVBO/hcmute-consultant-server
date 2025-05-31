FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/HcmuteConsultantServer.jar app.jar
ENV JAVA_OPTS="-Xms128m -Xmx384m -XX:+UseG1GC -XX:MaxMetaspaceSize=128m -XX:+HeapDumpOnOutOfMemoryError -XX:+UseStringDeduplication"
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]