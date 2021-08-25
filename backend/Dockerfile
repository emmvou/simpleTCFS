# Traditional docker build with a Dockerfile

FROM adoptopenjdk/openjdk11:alpine-jre

MAINTAINER Philippe Collet (philippe.collet@univ-cotedazur.fr)

ARG JAR_FILE

# Best practices: COPY is more transparent than ADD
COPY ${JAR_FILE} app.jar

# exposing the 8080 port to support external connections
EXPOSE 8080

# Starting the service
ENTRYPOINT ["java","-jar","/app.jar"]
