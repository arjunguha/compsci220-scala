submission
==========

Build Requirements
------------------

- Java 1.8
- Scala 2.11.0
- SBT 0.13.2

Runtime Requirements
--------------------

- Ubuntu 14.04
- Docker, included with Ubuntu (`apt-get install docker.io`)
- Docker configured to listen on a TCP socket.

  You can add the following line to `/etc/default/docker.io`:

      DOCKER_OPTS="-H tcp://127.0.0.1:2375 -H unix:///var/run/docker.sock"
- Java 1.8


Building
--------

From the terminal:

    $ sbt assembly

Testing
-------

Some test cases need the image _ubuntu:14.04_ from the Docker registry:

    $ docker.io pull ubuntu:14.04

To run the tests:

    $ sbt tests

