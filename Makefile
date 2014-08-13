.PHONY: all assembly ppa website

all:
	(cd support-code; sbt compile)
	(cd submission; sbt compile)

assembly:
	(cd support-code; sbt assembly)
	(cd submission; sbt assembly)

ppa: assembly
	(cd ppa; make)

website:
	(cd support-code; sbt doc)
	(cd website; make)

serve:
	(cd website; make serve)
