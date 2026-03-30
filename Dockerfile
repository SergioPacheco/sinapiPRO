FROM eclipse-temurin:11-jre-alpine
WORKDIR /app
COPY target/sinapiPRO-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]
