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
  lxterminal \
  xdg-utils \
  vim-gtk \
  emacs24 \
  software-properties-common

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
  SUBL_DEB_FILE=sublime-text_build-3083_i386.deb
else
  SUBL_DEB_FILE=sublime-text_build-3083_amd64.deb
fi
wget https://download.sublimetext.com/$SUBL_DEB_FILE
dpkg -i $SUBL_DEB_FILE
rm $SUBL_DEB_FILE

# Install JDK
echo debconf shared/accepted-oracle-license-v1-1 select true | \
  debconf-set-selections
add-apt-repository -y ppa:webupd8team/java
apt-get update
apt-get install -y oracle-java8-installer

# Install SBT
wget http://dl.bintray.com/sbt/debian/sbt-0.13.9.deb
dpkg -i sbt-0.13.9.deb
rm sbt-0.13.9.deb

# Fix broken deps
apt-get -f -y install


# Setup unattended upgrades
cat << EOF > /etc/apt/apt.conf.d/10periodic
APT::Periodic::Unattended-Upgrade "1";
EOF

# Remove junk
apt-get remove -y xterm
rm -rf /home/student/Templates
rm /var/cache/apt/archives/*.deb

# Setup sbt
mkdir -p /home/student/.sbt/plugins
cat <<EOF > /home/student/.sbt/plugins/plugins.sbt
addSbtPlugin("edu.umass.cs" % "cmpsci220" % "3.0.0")
EOF

cd /home/student
cat <<EOF > Main.scala
object Main extends App {
  println("SBT ready")
}
EOF
su student sbt run

rm -rf /home/student/project
rm /home/student/Main.scala


# Zero free space
cat /dev/zero > zero.fill; sync; sleep 1; sync; rm -f zero.fill
echo "DONE"
