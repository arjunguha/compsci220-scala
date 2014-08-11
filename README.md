This directory holds a Vagrant environment for hacking on the course software.

- It has the same GUI (Lubuntu) and Scala software that students use.

- It excludes utilities, such as text editors.

- It mounts the parent directory where this repository is checked out at
  `/home/vagrant/src`.

- If this repository is checked out to the `support-code` directory, then
  `scala220` is in `PATH` and works as expected.

## Provisioning

    $ vagrant up --provider virtualbox
