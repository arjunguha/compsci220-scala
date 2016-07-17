#!/bin/bash
set -x
set -e

apt-get update -q
apt-get install -y software-properties-common

echo debconf shared/accepted-oracle-license-v1-1 select true | \
  debconf-set-selections
add-apt-repository -y ppa:webupd8team/java

add-apt-repository -y ppa:webupd8team/sublime-text-3

echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823

apt-get update

apt-get upgrade -y

# This script configures the student virtual machine for CS220.
# It is meant to be run as root on a clean installation of AMD64 Ubuntu 14.04
# **Minimal** with no extra packages installed.
apt-get install -y \
  lubuntu-core \
  curl \
  lxterminal \
  xdg-utils \
  firefox \
  sublime-text-installer \
  oracle-java8-installer \
  sbt

# Allow sudo with password
adduser --disabled-password --gecos "" student

usermod -a -G sudo student
cat << EOF > /etc/sudoers
Defaults  env_reset
Defaults  mail_badpass
Defaults  secure_path="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"

root  ALL=(ALL:ALL) ALL
%admin ALL=(ALL) ALL
%sudo ALL= NOPASSWD:ALL
EOF

# Auto-Login
cat << EOF > /etc/lightdm/lightdm.conf
[SeatDefaults]
autologin-user=student
autologin-user-timeout=0
user-session=Lubuntu
greeter-session=lightdm-gtk-greeter
EOF

# Setup unattended upgrades
cat << EOF > /etc/apt/apt.conf.d/10periodic
APT::Periodic::Unattended-Upgrade "1";
EOF

# Remove junk
apt-get remove -y xterm
rm -rf /home/student/Templates
rm /var/cache/apt/archives/*.deb


# Zero free space
cat /dev/zero > zero.fill; sync; sleep 1; sync; rm -f zero.fill
echo "DONE"
