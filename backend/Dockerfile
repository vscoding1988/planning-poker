FROM openjdk:17-slim-buster

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]

#  docker build -t vs/spring-boot-base .
