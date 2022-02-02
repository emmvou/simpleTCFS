# Traditional docker build with a Dockerfile

FROM openjdk:11-slim-bullseye

LABEL org.opencontainers.image.authors="Philippe Collet philippe.collet@univ-cotedazur.fr"

ARG JAR_FILE

# Best practices: COPY is more transparent than ADD
COPY ${JAR_FILE} app.jar

COPY wait-for-it.sh /wait-for-it.sh

RUN chmod +x /wait-for-it.sh

# exposing the 8080 port to support external connections
EXPOSE 8080

# Starting the service (shell form of ENTRYPOINT used for substitution)
ENTRYPOINT java -jar /app.jar --bank.host.baseurl=$BANK_URL
