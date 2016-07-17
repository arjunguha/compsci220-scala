build:
	vagrant up
	VBoxManage controlvm compsci220-student-vm acpipowerbutton

export:
	VBoxManage export compsci220-student-vm -o compsci220.ova

upload:
	gsutil cp compsci220.ova gs://umass-compsci220/compsci220.ova
	gsutil acl ch -u AllUsers:R gs://umass-compsci220/compsci220.ova
