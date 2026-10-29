# SchoolChan backend
# Build:  docker build -f backend/Dockerfile -t schoolchan-backend .
# Run:    docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=local schoolchan-backend
FROM eclipse-temurin:21-jre

WORKDIR /app

# Build the jar in a separate stage so the runtime image stays small.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /build
COPY schoolchan/backend/gradle/ ./
COPY schoolchan/backend/gradlew ./
COPY schoolchan/backend/gradlew.bat ./
COPY schoolchan/backend/settings.gradle.kts schoolchan/backend/build.gradle.kts ./
RUN chmod +x gradlew
COPY schoolchan/backend/src ./src
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN mkdir -p /app/data
COPY --from=build /build/build/libs/*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
