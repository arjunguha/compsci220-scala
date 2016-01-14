HOST=tcp://10.240.0.3:2376

all:
	docker -H $(HOST) build -t student .
	docker -H $(HOST) tag student gcr.io/umass-cmpsci220/student
