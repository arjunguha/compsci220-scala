#!/bin/bash

for DIR in *; do
  if [ -d $DIR/template ]; then
    rm -f ../website/hw/$DIR.zip
    (cd $DIR; zip -r ../../website/hw/$DIR template -x@../exclude.txt)
  fi
done