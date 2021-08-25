#!/bin/bash

# Running the image as
#  - removing the container after exit (ease housekeeping because it's a POC)
#  -it interactive shell
# passing the URL of the TCF server as an environment variable (see Dockerfile)
docker run --env SERVER_URL=$1 --rm -it pcollet/tcf-spring-cli

# ^C to stop
