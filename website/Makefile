all:
	jekyll build

publish:
	rsync -avzr --delete _site/ arjun@people.cs.umass.edu:~/public_html/courses/compsci220-fall2016

serve:
	jekyll serve --watch
