VERSION=$(shell head -n 1 debian/changelog | grep --o '(.*)' | sed 's/(//' | sed 's/)//')
SUBMISSION_VER=$(shell grep 'version' submission/build.sbt | grep --o '\".*\"' | sed 's/\"//g')
SUPPORT_VER=$(shell grep 'version' support-code/build.sbt | grep --o '\".*\"' | sed 's/\"//g')
TARGET_BASE=targets
TARGET=$(TARGET_BASE)/cs220-$(VERSION)
LIB=$(TARGET)/usr/lib/cs220
BIN=$(TARGET)/usr/bin

all:
	#cd submission && sbt assembly
	#cd support-code && sbt assembly
	mkdir -p $(BIN)
	mkdir -p $(LIB)
	cp support-code/repl/target/scala-2.11/repl-assembly-$(SUPPORT_VER).jar $(LIB)/repl.jar
	cp submission/target/scala-2.10/submission-assembly-$(SUBMISSION_VER).jar $(LIB)/submit.jar
	cp scala220 $(BIN)/scala220
	cp submit220 $(BIN)/submit220
	cp -r debian $(TARGET)
	cd $(TARGET) && debuild -S
	cd $(TARGET_BASE) && dput ppa:arjun-guha/umass-cs220 cs220_$(VERSION)_source.changes

versions:
	echo $(SUBMISSION_VER)
