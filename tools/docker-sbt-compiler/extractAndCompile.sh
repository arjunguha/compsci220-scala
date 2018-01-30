# Assumes that there is a file project.zip in the home directory.
# The zip file should extract to the following dir structure:
#
# - build.sbt
# - project/{plugins.sbt, build.properties, assembly.sbt}
# - src/{main,test}/scala/<scala-files>

set -x

# Make sure the project dir is clean
rm -rf hw
mkdir -p hw

# unzip the project into hw
#unzip ./project.zip -d hw
tar -zxf project.tar.gz -C hw

# Compile the project
cd hw
sbt assembly

# sbt assembly should create a file project.jar in the project top level
