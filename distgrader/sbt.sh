#!/bin/bash
LOGFILE=grading.log

echo "Logging to $LOGFILE"

sbt -J-Xmx4G -Dorg.slf4j.simpleLogger.defaultLogLevel=info \
  -Dorg.slf4j.simpleLogger.showDateTime=true \
  -Dorg.slf4j.simpleLogger.dateTimeFormat="H:m:s.S" \
  -Dorg.slf4j.simpleLogger.logFile=$LOGFILE \
  -Dgrader.controller=10.9.0.100
