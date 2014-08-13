CMPSCI220 Course Materials
==========================

This repository holds all course materials for CMPSCI220. The root directory
holds a [Vagrant] environemnt that you should use for any course development,
from updating the website to hacking on code.

The Vagrant environment has the same GUI (Lubuntu), JVM, and Scala that students
use in the course, but excludes utilities such as text editors. The Vagrant
environment is mounts this repository at `/home/vagrant/src`. So, you can use a
text editor on the host to edit files.

## Building the course software

1. Run `vagrant up --provider virtualbox`. This will take several minutes
   the first time you do this.

2. Run `vagrant ssh`.

3. Within the SSH session, run `make`

If the build succeeds, you'll be able to run the `scala220` and `check220`
scripts that students use in the course VM, in addition to an `admin220`
script that you can use to create auto-graded assignments.

## Releasing a software update to students

We use an Ubuntu Personal Package Archive (PPA) to release software updates:

https://launchpad.net/~arjun-guha/+archive/ubuntu/umass-cs220

You'll need ask Arjun to grant you the ability to send updates. Once that's
setup, do the following:

1. Add the GPG key you use on Launchpad to `./gnupg`.

2. Add a new entry at the top of `ppa/debian/changelog`. You *have* to
   increment the version number, set a well-formatted date, and have two
   blank lines between each entry. See that file for several examples.

3. Run `cd ~/src; make ppa`.

## Creating a new course VM for students

The course VM is configured to install software from the Ubuntu PPA. If you
need to build a new course VM, ensure that the PPA is up-to-date, as described
above.

[FILL]

## Updating the website

Since the website is in Arjun's personal folder, you can't publish changes.
But, you can build and preview changes within the VM.

1. Run `make website`

2. Visit `http://localhost:4000` using a Web browser on your host machine.

If you can publish the website, do `cd website; publish`

## Writing a new homework assignment

[FILL]


[Vagrant]: http://www.vagrantup.com