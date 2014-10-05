Vagrant.require_version ">= 1.6.0", "< 1.7.0"

Vagrant.configure("2") do |config|

  config.vm.box = "puppetlabs/ubuntu-14.04-64-puppet"

  config.vm.provider "virtualbox" do |v|
    v.memory = 3072
    v.cpus = 2
    v.gui = true
  end

  config.vm.provider "vmware_fusion" do |v|
    v.vmx["memsize"] = "3072"
    v.vmx["numvcpus"] = "2"
    v.gui = true
  end

  config.vm.provision :shell, path: "bootstrap.sh"

  config.vm.synced_folder ".", "/home/vagrant/src"
  config.vm.synced_folder "./gnupg", "/home/vagrant/.gnupg"

  config.vm.hostname = "devvm"

  config.vm.network "forwarded_port", guest: 4000, host: 4000

  config.ssh.forward_x11 = true

end
