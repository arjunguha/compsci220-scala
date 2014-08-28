VERSION=$(shell head -n 1 debian/changelog | grep --o '(.*)' | sed 's/(//' | sed 's/)//')
TARGET_BASE=targets
TARGET=$(TARGET_BASE)/cs220-$(VERSION)
LIB=$(TARGET)/usr/lib/cs220
BIN=$(TARGET)/usr/bin

all:
	#cd submission && sbt assembly
	#cd support-code && sbt assembly
	mkdir -p $(BIN)
	mkdir -p $(LIB)
	cp ../support-code/target/scala-2.11/cmpsci220.jar $(LIB)/cmpsci220.jar
	cp ../submission/target/scala-2.11/submission.jar $(LIB)/submit.jar
	cp ../support-code/bin/scala220 $(BIN)/scala220
	cp ../submission/bin/check220 $(BIN)/check220
	cp -r ../website/hw/_grading/sanity $(LIB)/sanity
	cp -r debian $(TARGET)
	cd $(TARGET) && debuild -S
	cd $(TARGET_BASE) && dput ppa:arjun-guha/umass-cs220 cs220_$(VERSION)_source.changes

versions:
	echo $(SUBMISSION_VER)
