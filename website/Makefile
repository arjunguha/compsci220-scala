all: lib
	jekyll build

lib: ../support-code/lib/target/scala-2.11/api
	rsync -avzr --delete ../support-code/lib/target/scala-2.11/api lib

publish: all
	rsync -avzr --delete _site/ arjun@people.cs.umass.edu:~/public_html/courses/cmpsci220-fall2014
