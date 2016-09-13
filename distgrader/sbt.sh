#!/bin/bash
if [[ `uname` -eq "Darwin" ]]; then
  LOGFILE=~/Library/Logs/grading.log
else
  LOGFILE=grading.log
fi

echo "Logging to $LOGFILE"

sbt -J-Xmx4G -Dorg.slf4j.simpleLogger.defaultLogLevel=info \
  -Dorg.slf4j.simpleLogger.showDateTime=true \
  -Dorg.slf4j.simpleLogger.dateTimeFormat="H:m:s.S" \
  -Dorg.slf4j.simpleLogger.logFile=$LOGFILE
