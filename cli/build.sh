#!/bin/bash

#Preparing environment
echo "Compiling the TCF Spring CLI"
mvn -q -DskipTests clean package
echo "Done"

# building the docker image
docker build --build-arg JAR_FILE=target/cli-0.0.1-SNAPSHOT.jar -t pcollet/tcf-spring-cli .
