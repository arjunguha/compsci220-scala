#!/bin/bash
#!/bin/bash
ARGS=$@

if [[ `uname` -eq "Darwin" ]]; then
  echo "Logging to ~/Library/Logs/grading.log"
  LOGFILE=~/Library/Logs/grading.log
else
  LOGFILE=rehearsal.log
fi

sbt -J-Xmx4G -Dorg.slf4j.simpleLogger.defaultLogLevel=info \
  -Dorg.slf4j.simpleLogger.logFile=$LOGFILE