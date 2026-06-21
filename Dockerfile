FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-26-jdk -y
COPY . .

RUN apt-get install maven -y
RUN mvn clean install

FROM openjdk:26-jdk-slim

EXPOSE 8080

COPY --from=build /target/priorium-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]