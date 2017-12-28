---
layout: default
title: Course Software
---

To do the homework and projects for this class, you'll need Scala and some
course-specific software. We **highly recommend** using an linux or a mac os
environment. If you have a Windows environment, you can setup a *virtual
machine*.

Learning to work in a linux-like environment is an essential skill for a
software developer. Additionally, several courses in the ciriculum asumme
knowledge on linux.

## Mac OS X

1. Install [Homebrew](http://brew.sh).

      `/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"`

2. Install Java from the Terminal:

      `brew cask install java`

3. Install SBT from the Terminal:

      `brew install sbt`

5. To verify that everything installed, type `sbt` to start SBT.

* * *

## Linux (Ubuntu):

1. Install Java 8:

       sudo add-apt-repository -y ppa:webupd8team/java
       sudo apt-get update
       sudo apt-get install -y oracle-java8-installer

2. Install SBT:

       echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
       sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
       sudo apt-get update
       sudo apt-get install sbt

3. To verify that everything installed, type `sbt` to start SBT.

* * *

## Virtual Machine

To use the [virtual machine], you will need at least 4 GB of RAM and a modern
processor. (For the curious, this virtual machine is running [Lubuntu] Linux
14.04 32-bit.)

1. Download and install [VirtualBox]

2. Download [compsci220.ova] (1.1 GB)

3. Double-click on the `compsci220.ova` file you downloaded above (or, select
   *Import Appliance* from the File menu and select the file). When you do,
   VirtualBox will start and display a prompt called *Appliance Settings*.
   Do not change any settings. Just click the *Import* button. When importing
   completes, the virtual machine will appear in VirtualBox, as shown below:

   <img src="virtualbox.png">

4. Select the CS220 virtual machine and click the *Start* button. If the
   machine starts successfully, you will see the desktop shown below:

   <img src="vm-desktop.png">

5. To test that everything is working correctly, start Firefox within
   the virtual machine and ensure you can browse the Web. You'll find a link
   to Firefox within the start menu in the bottom-left corner:

   <img src="vm-google-chrome.png">

* * *

## Windows

If you are having trouble setting up the virtual machine, you may also directly
setup Scala on your windows machines.

1. [Install Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

   If you already have Java installed, you need to ensure that it is Java 8
   or higher.

2. Install [SBT](http://www.scala-sbt.org)

3. Install [Cygwin](http://www.cygwin.com).

4. Verify that everything installed by typing `sbt` to start SBT from the
   Cygwin terminal.

### Known Issues:

* `sbt command not found`

    _Known Fix:_ Restart the computer

* Backspace not working

    _Known Fix:_ Left-Click on the top left corner of the Cygwin window (the Cygwin icon), then click on Options > Keys. Check the "Back arrow sends ^H"

    <img src="cygwin_backspace_fix.png">


[virtual machine]: http://en.wikipedia.org/wiki/Virtual_machine
[VirtualBox]: https://www.virtualbox.org/wiki/Downloads
[compsci220.ova]: https://storage.googleapis.com/umass-compsci220/compsci220.ova
[Lubuntu]: http://lubuntu.net
