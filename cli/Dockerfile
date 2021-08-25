# Traditional docker build with a Dockerfile

FROM adoptopenjdk/openjdk11:alpine-jre

MAINTAINER Philippe Collet (philippe.collet@univ-cotedazur.fr)

ARG JAR_FILE

# Best practices: COPY is more transparent than ADD
COPY ${JAR_FILE} app.jar

# Starting the service (shell form of ENTRYPOINT used for substitution)
ENTRYPOINT java -jar /app.jar --tcf.host.baseurl=$SERVER_URL
