# Stage 1: Dependencies (cached unless pom.xml changes)
ARG MAVEN_IMAGE=maven:3.9.11-eclipse-temurin-25
ARG JRE_IMAGE=eclipse-temurin:25-jre

FROM ${MAVEN_IMAGE} AS deps
WORKDIR /workspace
COPY pom.xml .
COPY .mvn .mvn
RUN mvn dependency:go-offline -s .mvn/settings.xml -B -q

# Stage 2: Build
FROM deps AS build
COPY src src
RUN mvn package -s .mvn/settings.xml -B -q -DskipTests

# Stage 3: Runtime (minimal)
FROM ${JRE_IMAGE} AS runtime
WORKDIR /app

# Non-root user
RUN groupadd -r appuser && useradd -r -g appuser -d /app appuser
RUN chown -R appuser:appuser /app
USER appuser

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080 8081

ENTRYPOINT ["java", "--enable-preview", "-XX:+UseZGC", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
