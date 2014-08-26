FROM ubuntu:14.04
MAINTAINER Arjun Guha <arjun@cs.umass.edu>

RUN apt-get update
RUN apt-get upgrade -yq
RUN apt-get install -yq wget software-properties-common

# Install JDK
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN apt-get install -yq oracle-java8-installer

# Install SBT
RUN wget http://dl.bintray.com/sbt/debian/sbt-0.13.5.deb
RUN dpkg -i sbt-0.13.5.deb
RUN rm sbt-0.13.5.deb

# Install Scala
RUN wget http://downloads.typesafe.com/scala/2.11.2/scala-2.11.2.deb
RUN dpkg -i scala-2.11.2.deb
RUN rm scala-2.11.2.deb

# Install CS220 software
ADD cmpsci220.jar /usr/lib/cs220/cmpsci220.jar
ADD scala220 /usr/bin/scala220
RUN chmod a+x /usr/bin/scala220

ADD docker220 /usr/local/bin/docker220
RUN chmod a+x /usr/local/bin/docker220