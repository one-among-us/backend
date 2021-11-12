# syntax=docker/dockerfile:1

FROM gradle:7.2-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:11

RUN mkdir /app
RUN mkdir /app/libs
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
COPY --from=build /home/gradle/src/build/dependencies/*.jar /app/libs/

EXPOSE 43482
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-cp", "/app/app.jar:/app/libs/*", "org.hydev.ApplicationKt"]
