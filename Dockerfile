FROM openjdk:8-jdk

RUN mkdir /app

COPY ./build/install/docker/ /app/

WORKDIR /app/bin
