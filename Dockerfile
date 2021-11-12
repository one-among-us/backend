# syntax=docker/dockerfile:1

FROM gradle:7.2-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:11

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
