# Build stage
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /home/gradle/project

# copy project
COPY --chown=gradle:gradle . .

# normalize line endings and make wrapper executable, then build
RUN sed -i 's/\r$//' ./gradlew || true \
    && chmod +x ./gradlew \
    && ./gradlew clean build -x test --no-daemon

# Ensure a single predictable jar name
RUN set -eux; \
    JAR=$(ls build/libs/*.jar | head -n 1); \
    test -n "$JAR"; \
    cp "$JAR" app.jar

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /home/gradle/project/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]