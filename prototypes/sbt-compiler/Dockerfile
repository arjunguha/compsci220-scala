# NOTE(rachit): Minimize the number of RUN and DELETE commands.
# https://stackoverflow.com/questions/35154219/rebuild-docker-image-from-specific-step

FROM ubuntu:14.04
MAINTAINER Rachit Nigam <rachit.nigam12@gmail.com>
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
  apt-get install -yq wget software-properties-common curl

# Install JDK
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -yq oracle-java8-installer

# Install SBT
RUN wget http://dl.bintray.com/sbt/debian/sbt-0.13.11.deb && \
  dpkg -i sbt-0.13.11.deb && \
  rm sbt-0.13.11.deb

# Install Scala
RUN wget http://downloads.typesafe.com/scala/2.11.8/scala-2.11.8.deb && \
  dpkg -i scala-2.11.8.deb && \
  rm scala-2.11.8.deb

# Install node
RUN curl -sL https://deb.nodesource.com/setup_8.x | bash - && \
  sudo apt-get install -yq nodejs

# Create user account
RUN adduser --disabled-password --gecos "" student
USER student
WORKDIR /home/student

# Needed to copy files in
RUN mkdir hw

# Pre-fetch typical dependencies
RUN mkdir -p /home/student/scala-init/project
COPY plugins.sbt build.properties /home/student/scala-init/project/
COPY Init.scala /home/student/scala-init/
WORKDIR /home/student/scala-init
RUN sbt run

WORKDIR /home/student
COPY ./extractAndCompile.sh /home/student

COPY action /action

ENTRYPOINT ["/usr/bin/node", "/action/dist/index.js"]
