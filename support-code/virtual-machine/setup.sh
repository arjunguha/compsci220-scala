#!/bin/bash

# This script configures the student virtual machine for CS220.
# It is meant to be run as root on a clean installation of AMD64 Ubuntu 14.04
# **Minimal** with no extra packages installed.
apt-get install -y \
  lubuntu-core \
  curl \
  virtualbox-guest-x11 \
  lxterminal \
  xdg-utils \
  docker.io \
  software-properties-common

# Configure Docker
cat << EOF > /etc/default/docker.io
DOCKER_OPTS="-H tcp://127.0.0.1:2375 -H unix:///var/run/docker.sock"
EOF
sed -i 's/GRUB_CMDLINE_LINUX=\"\"/GRUB_CMDLINE_LINUX=\"cgroup_enable=memory swapaccount=1\"/' /etc/default/grub
update-grub

adduser --disabled-password --gecos "" student

# Auto-Login
cat << EOF > /etc/lightdm/lightdm.conf
[SeatDefaults]
autologin-user=student
autologin-user-timeout=0
user-session=Lubuntu
greeter-session=lightdm-gtk-greeter
EOF

# Install Chrome
wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
dpkg -i google-chrome-stable_current_amd64.deb
rm google-chrome-stable_current_amd64.deb

# Install Sublime Text
wget http://c758482.r82.cf2.rackcdn.com/sublime-text_build-3059_amd64.deb
dpkg -i sublime-text_build-3059_amd64.deb
rm sublime-text_build-3059_amd64.deb

# Install JDK
echo debconf shared/accepted-oracle-license-v1-1 select true | \
  debconf-set-selections
add-apt-repository -y ppa:webupd8team/java
apt-get update
apt-get install -y oracle-java8-installer

# Install SBT
wget http://dl.bintray.com/sbt/debian/sbt-0.13.5.deb
dpkg -i sbt-0.13.5.deb
rm sbt-0.13.5.deb

# Install Scala
wget http://downloads.typesafe.com/scala/2.11.2/scala-2.11.2.deb
dpkg -i scala-2.11.2.deb
rm scala-2.11.2.deb

# Install CS220 software
add-apt-repository -y ppa:arjun-guha/umass-cs220
apt-get update
apt-get install -y cs220

# Setup unattended upgrades
cat << EOF > /etc/apt/apt.conf.d/10periodic
APT::Periodic::Unattended-Upgrade "1"
EOF
cat << EOF /etc/apt/apt.conf.d/50-unattended-upgrades
Unattended-Upgrade::Allowed-Origins {
       "${distro_id}:${distro_codename}-security";
       "LP-PPA-arjun-guha-umass-cs220 trusty";
};

# Remove junk
apt-get remove -y xterm
rm -rf /home/student/Templates
rm /var/cache/apt/archives/*.deb

# Zero free space
cat /dev/zero > zero.fill; sync; sleep 1; sync; rm -f zero.fill
shutdown -h now
