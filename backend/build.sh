#!/bin/bash

#Preparing environment
echo "Compiling the TCF Spring backend"
mvn clean package
echo "Done"

# building the docker image
docker build --build-arg JAR_FILE=target/simpleTCFS-0.0.1-SNAPSHOT.jar -t pcollet/tcf-spring-backend .
