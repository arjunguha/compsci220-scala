#!/bin/bash
HASH1=`gsutil hash -m cmpsci220.ova 2> /dev/null | grep "Hash (md5)" | cut -f 2 -d ":" | tr -d '\t'`
HASH2=`gsutil stat gs://umass-cmpsci220-artifacts/cmpsci220.ova | grep "Hash (md5)" |  cut -f 2 -d ":" | tr -d '\t'`

echo $HASH1
echo $HASH2
if [ "$HASH1" != "$HASH2" ]; then
  echo "Uploading disk image"
  gsutil cp cmpsci220.ova gs://umass-cmpsci220-artifacts/cmpsci220.ova
  gsutil acl ch -u AllUsers:R gs://umass-cmpsci220-artifacts/cmpsci220.ova
fi


