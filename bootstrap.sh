set -x

VIRTUALBOX_VERSION=4.3.14
SCALA_VERSION=2.11.2
SBT_VERSION=0.13.5
JAVA_VERSION=8

apt-get update -q
apt-get upgrade -yq

# Install latest guest additions. These are needed to avoid JavaFX warnings
# regarding OpenGL.
apt-get purge -yq virtualbox-guest-x11
apt-get install -yq xserver-xorg gcc dkms
wget -q http://download.virtualbox.org/virtualbox/${VIRTUALBOX_VERSION}/VBoxGuestAdditions_${VIRTUALBOX_VERSION}.iso
mkdir guest-additions
mount -o loop VBoxGuestAdditions_${VIRTUALBOX_VERSION}.iso guest-additions
(cd guest-additions; echo -e "yes\n" | ./VBoxLinuxAdditions.run)
rm VBoxGuestAdditions_${VIRTUALBOX_VERSION}.iso
apt-get purge -yq gcc
rm -rf /home/vagrant/guest-additions

apt-get install -yq \
  lubuntu-core \
  curl \
  lxterminal \
  xdg-utils \
  docker.io \
  packaging-dev \
  ruby-dev \
  debuild \
  gcc \
  nodejs \
  software-properties-common

echo 'DOCKER_OPTS="-H tcp://127.0.0.1:2375 -H tcp://127.0.0.1:4243 -H unix:///var/run/docker.sock"' > /etc/default/docker.io
service docker.io restart

sed -i 's/GRUB_CMDLINE_LINUX=\"\"/GRUB_CMDLINE_LINUX=\"cgroup_enable=memory swapaccount=1\"/' /etc/default/grub
update-grub

add-apt-repository ppa:webupd8team/java
apt-get update -q

echo debconf shared/accepted-oracle-license-v1-1 select true | \
  debconf-set-selections

apt-get install -yq oracle-java${JAVA_VERSION}-installer

wget -q http://www.scala-lang.org/files/archive/scala-${SCALA_VERSION}.deb
wget -q http://dl.bintray.com/sbt/debian/sbt-${SBT_VERSION}.deb
dpkg -i sbt-${SBT_VERSION}.deb
dpkg -i scala-${SCALA_VERSION}.deb
rm sbt-${SBT_VERSION}.deb scala-${SCALA_VERSION}.deb

# Auto-Login
cat << EOF > /etc/lightdm/lightdm.conf
[SeatDefaults]
autologin-user=vagrant
autologin-user-timeout=0
user-session=Lubuntu
greeter-session=lightdm-gtk-greeter
EOF

apt-get purge -yq xterm

cat << EOF >> /home/vagrant/.profile
PATH=/home/vagrant/src/submission/bin:/home/vagrant/src/support-code/bin:$PATH
export ASSIGNMENTS_PATH=/home/vagrant/src/website/hw/_grading/sanity/settings.conf
EOF

gem install jekyll

echo "Run vagrant reload to load the GUI and enable Docker memory limits."