#!/bin/bash

#Preparing environment
./compile.sh

docker build -t pcollet/tcf-bank-service .
