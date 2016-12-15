#!/bin/bash
set -e
LOGFILE=grading.log
echo "Logging to $LOGFILE"

if [[ `uname` == "Darwin" ]]; then 
    IFACE=`route get 10.9.0.1 | awk '/interface/ {print $2}'`
    IP=`ifconfig $IFACE | awk '/inet/ { print $2 }'`
else
    echo "Unsupported machine type: `uname`"
    exit 2
fi

echo "Using local IP address $IP for the controller."

sbt -J-Xmx4G -Dorg.slf4j.simpleLogger.defaultLogLevel=info \
  -Dorg.slf4j.simpleLogger.showDateTime=true \
  -Dorg.slf4j.simpleLogger.dateTimeFormat="H:m:s.S" \
  -Dorg.slf4j.simpleLogger.logFile=$LOGFILE \
  -Dgrader.controller=$IP
