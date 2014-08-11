Vagrant.require_version ">= 1.6.0", "< 1.7.0"

Vagrant.configure("2") do |config|

  config.vm.box = "ubuntu/trusty64"

  config.vm.provider "virtualbox" do |v|
    v.memory = 2048
    v.cpus = 2
    v.gui = true
  end

  config.vm.provision :shell, path: "bootstrap.sh"

  config.vm.synced_folder ".", "/home/vagrant/src"

  config.vm.hostname = "devvm"


end