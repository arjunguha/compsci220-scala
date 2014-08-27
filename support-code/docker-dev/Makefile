all:  cmpsci220.jar scala220
	docker.io build -t arjunguha/cs220 .

cmpsci220.jar: ../target/scala-2.11/cmpsci220.jar
	cp ../target/scala-2.11/cmpsci220.jar cmpsci220.jar

scala220: ../bin/scala220
	cp ../bin/scala220 scala220