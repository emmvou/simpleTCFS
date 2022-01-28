# Traditional docker build with a Dockerfile

FROM openjdk:11-slim-bullseye

LABEL org.opencontainers.image.authors="Philippe Collet philippe.collet@univ-cotedazur.fr"

ARG JAR_FILE

# Best practices: COPY is more transparent than ADD
COPY ${JAR_FILE} app.jar

COPY wait-for-it.sh /wait-for-it.sh

# Starting the service (shell form of ENTRYPOINT used for substitution)
ENTRYPOINT java -jar /app.jar --tcf.host.baseurl=$SERVER_URL
