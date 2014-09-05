#!/bin/bash

set -x

apt-get update
apt-get upgrade -y

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
  vim-gtk \
  emacs24 \
  software-properties-common

# Configure Docker
cat << EOF > /etc/default/docker.io
DOCKER_OPTS="-H tcp://127.0.0.1:2375 -H unix:///var/run/docker.sock"
EOF
sed -i 's/GRUB_CMDLINE_LINUX=\"\"/GRUB_CMDLINE_LINUX=\"cgroup_enable=memory swapaccount=1\"/' /etc/default/grub
update-grub

adduser --disabled-password --gecos "" student

# Allow sudo with password
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

# Install Chrome
wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
if [ `arch` == "x86_64"]; then
  CHROME_DEB_FILE=google-chrome-stable_current_amd64.deb
else
  CHROME_DEB_FILE=google-chrome-stable_current_i386.deb
fi
 wget https://dl.google.com/linux/direct/$CHROME_DEB_FILE
dpkg -i $CHROME_DEB_FILE
rm $CHROME_DEB_FILE
apt-get -f -y install

# Install Sublime Text
if [ `arch` == "x86_64" ]; then
  SUBL_DEB_FILE=sublime-text_build-3059_amd64.deb
else
  SUBL_DEB_FILE=sublime-text_build-3059_i386.deb
fi
wget http://c758482.r82.cf2.rackcdn.com/$SUBL_DEB_FILE
dpkg -i $SUBL_DEB_FILE
rm $SUBL_DEB_FILE

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

# Fix broken deps
apt-get -f -y install

# Install CS220 software
add-apt-repository -y ppa:arjun-guha/umass-cs220
apt-get update
apt-get install -y cs220

# Install CS220 docker image
if [ `arch` == "x86_64" ]; then
  docker.io pull arjunguha/cs220
fi

# Setup unattended upgrades
cat << EOF > /etc/apt/apt.conf.d/10periodic
APT::Periodic::Unattended-Upgrade "1";
EOF

cat << EOF /etc/apt/apt.conf.d/50-unattended-upgrades
Unattended-Upgrade::Allowed-Origins {
       "${distro_id}:${distro_codename}-security";
       "LP-PPA-arjun-guha-umass-cs220 trusty";
};
EOF

# Remove junk
apt-get remove -y xterm
rm -rf /home/student/Templates
rm /var/cache/apt/archives/*.deb

# Zero free space
cat /dev/zero > zero.fill; sync; sleep 1; sync; rm -f zero.fill
echo "DONE"
