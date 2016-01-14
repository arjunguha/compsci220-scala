#!/bin/sh

apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
echo "deb https://apt.dockerproject.org/repo ubuntu-trusty main" > /etc/apt/sources.list.d/docker.list
apt-get update
apt-get install -y docker-engine

cat << EOF > /etc/default/docker
DOCKER_OPTS="-H tcp://0.0.0.0:2376"
EOF

service docker restart

apt-get install -yq wget software-properties-common
echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
add-apt-repository -y ppa:webupd8team/java
apt-get update
apt-get install -yq oracle-java8-installer

apt-get install -yq supervisor
