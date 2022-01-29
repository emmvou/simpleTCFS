# The Cookie Factory... in Spring (aka the simple TCFS)

  * Author: Philippe Collet
  * Author: Nassim Bounouas
  * Reviewer: Anne-Marie Déry
  * some code and doc borrowed from the original Cookie Factory by Sebastiem Mosser, last fork being [https://github.com/collet/4A_ISA_TheCookieFactory](https://github.com/collet/4A_ISA_TheCookieFactory)

Simple mono-repo TCF in Spring

This case study is used to illustrate the different technologies involved in the _Introduction to Software Architecture_  course given at Polytech Nice - Sophia Antipolis at the graduate level. This demonstration code requires the following software to run properly:

  * Build & Spring environment configuration: Maven >=3.8.1
  * J2E implementation language: Java >=11 or above (Java language level is set to Java 11)
  * .Net implementation language: Mono >=6.12

## Product vision

_The Cookie Factory_ (TCF) is a major bakery brand in the USA. The _Cookie on Demand_ (CoD) system is an innovative service offered by TCF to its customer. They can order cookies online thanks to an application, and select when they'll pick-up their order in a given shop. The CoD system ensures to TCF's happy customers that they'll always retrieve their pre-paid warm cookies on time.

## Chapters

  1. Architecture
  2. Business Components
  3. Controllers
  4. Testing
  5. Persistence
  6. AOP and monitoring

## How to use this repository

  * The `develop` branch (the default one) represents the system under development. 
    * The RELEASE_TO_BE_DONE branch contains the code that implements the system without persistence;
    * Other releases will be out soon.

The following "build and run" documentation is divided in three versions from bare run to "everything in a container" run.

### Basic build and run

The first step is to build the backend and the cli. This can be done manually using the command:

    mvn clean package
 
from both folders (it will generate the corresponding jar into the target folder).

To run the server (from the corresponding folder):

    mvn spring-boot:run
    
or

    java -jar target/simpleTCFS-0.0.1-SNAPSHOT.jar

To run the cli (from the corresponding folder):

    mvn spring-boot:run
    
or

    java -jar target/cli-0.0.1-SNAPSHOT.jar

At startup the cli must provide the following prompt :

    shell:>

Running the command `help` will guide you in the CLI usage.

### Containerized backend

In this version, we will run the cli as previously, however we will run the backend in a docker container.

  * To build the backend docker image from the corresponding folder, the script `build.sh` can be used or directly the command `docker build --build-arg JAR_FILE=target/simpleTCFS-0.0.1-SNAPSHOT.jar -t pcollet/tcf-spring-backend .`

  * To run it the script `run.sh` can be used or directly `docker run --rm -d -p 8080:8080 pcollet/tcf-spring-backend`.

Note: It's necessary to stop the "basic" version of the backend to release the 8080 port.

### Everything containerized and composed

We will now run both the backend and the CLI into docker. It requires to build the cli docker image (the backend's one is considered built during the previous step).

To build the cli docker image from the corresponding folder, the script `build.sh` can be used or directly the command `docker build --build-arg JAR_FILE=target/cli-0.0.1-SNAPSHOT.jar -t pcollet/tcf-spring-cli .`

The whole system can now be deployed locally from the root folder using the command:

    docker-compose up -d
    
after few seconds:

    docker attach cli

enables to use the containerized cli (see docker-compose.yml and the devops lecture for more information).

Note that you cannot run the two docker images separately and expect them to communicate with each other, each one being isolated in its own container. That's one of the main purpose of `docker-compose` to enable composition of container, with by default a shared network.