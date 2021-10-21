FROM openjdk:17-jdk

RUN mkdir /app

COPY ./build/install/docker/ /app/

WORKDIR /app/bin
