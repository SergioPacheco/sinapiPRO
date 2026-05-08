# Stage 1: Dependencies (cached unless pom.xml changes)
FROM maven:3.9.11-eclipse-temurin-25 AS deps
WORKDIR /workspace
COPY pom.xml .
COPY .mvn .mvn
RUN mvn dependency:go-offline -s .mvn/settings.xml -B -q

# Stage 2: Build
FROM deps AS build
COPY src src
RUN mvn package -s .mvn/settings.xml -B -q -DskipTests \
    && java -Djarmode=tools -jar target/*.jar extract --layers --destination extracted

# Stage 3: Runtime (minimal)
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

# Non-root user
RUN groupadd -r appuser && useradd -r -g appuser -d /app appuser
RUN chown -R appuser:appuser /app
USER appuser

# Copy layers in order of change frequency (most stable first)
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

EXPOSE 8080 8081

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health/liveness || exit 1

ENTRYPOINT ["java", "-XX:+UseZGC", "-XX:MaxRAMPercentage=75.0", "org.springframework.boot.loader.launch.JarLauncher"]
