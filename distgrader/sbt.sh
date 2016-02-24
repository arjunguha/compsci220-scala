#!/bin/bash
if [[ `uname` -eq "Darwin" ]]; then
  LOGFILE=~/Library/Logs/grading.log
else
  LOGFILE=grading.log
fi

echo "Logging to ~/Library/Logs/$LOGFILE"

sbt -J-Xmx4G -Dorg.slf4j.simpleLogger.defaultLogLevel=info \
  -Dorg.slf4j.simpleLogger.logFile=$LOGFILE