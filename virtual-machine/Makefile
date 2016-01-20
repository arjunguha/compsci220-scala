build:
	vagrant up
	VBoxManage controlvm cmpsci220-student-vm acpipowerbutton

export:
	VBoxManage export cmpsci220-student-vm cmpsci220.ova

upload:
	gsutil cp cmpsci220.ova gs://umass-cmpsci220-artifacts/cmpsci220.ova
	gsutil acl ch -u AllUsers:R gs://umass-cmpsci220-artifacts/cmpsci220.ova
