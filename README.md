# simpleTCFS

Simple mono-repo TCF in Spring

The following "build and run" documentation is divided in three versions from bare run to "everything in a container" run.
## Basic build and run

The first step is to build the backend and the cli. This can be done manually using the command `mvn clean package` from both folders (it will generate the corresponding jar into the target folder).

- To run the server (from the corresponding folder) : `mvn spring-boot:run` or `java -jar target/simpleTCFS-0.0.1-SNAPSHOT.jar`

- To run the cli (from the corresponding folder) : `mvn spring-boot:run` or `java -jar target/cli-0.0.1-SNAPSHOT.jar`

At startup the cli must provide the following prompt :
```
shell:>
```

Running the command `help` will guide you in the CLI usage.

## Containerized backend

In this version, we will run the cli as previously, however we will run the backend in a docker container.

To build the backend docker image from the corresponding folder, the script `build.sh` can be used or directly the command `docker build --build-arg JAR_FILE=target/simpleTCFS-0.0.1-SNAPSHOT.jar -t pcollet/tcf-spring-backend .`

To run it the script `run.sh` can be used or directly `docker run --rm -d -p 8080:8080 pcollet/tcf-spring-backend`.

Note : It's necessary to stop the "basic" version of the backend to release the 8080 port.

## Everything containerized

We will now run both the backend and the CLI into docker. It requires to build the cli docker image (the backend's one is considered built during the previous step).

To build the cli docker image from the corresponding folder, the script `build.sh` can be used or directly the command `docker build --build-arg JAR_FILE=target/cli-0.0.1-SNAPSHOT.jar -t pcollet/tcf-spring-cli .`

The whole system can now be deployed locally from the root folder using the command `docker-compose up -d` after few seconds `docker attach cli` permits to use the containerized cli. (see docker-compose.yml and the devops lecture for more information)