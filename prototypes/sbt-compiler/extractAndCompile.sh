set -x

SUBMISSION="$1"

# Create a tmp directory
TMP=$(mktemp -d)

# Cleanup function
function cleanup {
  rm -rf $TMP
}
trap cleanup EXIT

# Copy the submission over
cp $SUBMISSION $TMP
cd $TMP
tar -xvf $SUBMISSION

sbt "; compile; assembly"

# If compilation failed, return exit code 1
if [[ $? != 0 ]]; then
  exit 1
fi

# Assumes that the assembly action creates 'submission.jar' in cwd
cp ./submission.jar $HOME

exit 0
