# syntax=docker/dockerfile:1

FROM gradle:7.4.2-jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build
RUN rm /home/gradle/src/build/libs/*-plain.jar

FROM adoptopenjdk/openjdk11-openj9:jre-11.0.14_9_openj9-0.30.0-alpine

RUN mkdir /app
RUN mkdir /app/libs
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

WORKDIR /app
EXPOSE 43482
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
