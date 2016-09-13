build:
	docker build -t student .
	docker tag -f student gcr.io/arjun-umass/grading-compsci220
	gcloud docker push gcr.io/arjun-umass/grading-compsci220
