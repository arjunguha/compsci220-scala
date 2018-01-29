FROM ubuntu:14.04
MAINTAINER Rachit Nigam <rachit.nigam12@gmail.com>

RUN apt-get update
RUN apt-get upgrade -yq
RUN apt-get install -yq wget software-properties-common curl

# Install JDK
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN apt-get install -yq oracle-java8-installer

# Install SBT
RUN wget http://dl.bintray.com/sbt/debian/sbt-0.13.11.deb
RUN dpkg -i sbt-0.13.11.deb
RUN rm sbt-0.13.11.deb

# Install Scala
RUN wget http://downloads.typesafe.com/scala/2.11.8/scala-2.11.8.deb
RUN dpkg -i scala-2.11.8.deb
RUN rm scala-2.11.8.deb

# Create user account
RUN adduser --disabled-password --gecos "" student
USER student
WORKDIR /home/student

# Needed to copy files in
RUN mkdir hw

# Pre-fetch typical dependencies
RUN mkdir /home/student/scala-init
RUN mkdir /home/student/scala-init/project
COPY plugins.sbt /home/student/scala-init/project/plugins.sbt
COPY Init.scala /home/student/scala-init/Init.scala
COPY build.sbt /home/student/scala-init/build.sbt
WORKDIR /home/student/scala-init
RUN sbt run

# Install node
RUN curl -sL https://deb.nodesource.com/setup_8.x | bash -
RUN apt-get install -y nodejs

WORKDIR /home/student

COPY action /action

ENTRYPOINT ["/usr/bin/node", "/action/js/index.js"]
