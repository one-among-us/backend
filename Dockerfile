# syntax=docker/dockerfile:1

FROM gradle:7.6-jdk17-alpine as cache
COPY build.gradle.kts gradle.properties settings.gradle.kts versions.properties /src/
WORKDIR /src
RUN gradle clean build -i --stacktrace; exit 0

FROM gradle:7.6-jdk17-alpine AS build
COPY --from=cache /home/gradle/.gradle /home/gradle/.gradle

COPY . /src/
WORKDIR /src
RUN gradle clean build -i --no-daemon

FROM eclipse-temurin:17-alpine

RUN mkdir -p /app/libs
COPY --from=build /src/build/libs/*.jar /app/app.jar

WORKDIR /app
EXPOSE 43482
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
