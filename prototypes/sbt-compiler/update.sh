#!/bin/bash

IMAGE='rachitnigam/compsci220'
ACTION='sbt-compiler'

docker build -t $IMAGE .
docker push $IMAGE

bx wsk action update sbt-compiler --docker=$IMAGE
