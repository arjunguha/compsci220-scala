#!/bin/bash
adduser --disabled-password --gecos "" student
cd /home/student

TOKEN=`curl http://metadata/computeMetadata/v1/instance/service-accounts/default/token -H "X-Google-Metadata-Request: True" |  sed -nr 's/^.*"access_token":\s*"(.*)",.*$/\1/p'`

wget --header "Authorization: OAuth $TOKEN" umass-cmpsci220-artifacts.storage.googleapis.com/worker.jar

cat << EOF > /etc/supervisor/conf.d/supervisord.conf
[supervisord]
nodaemon=false

[program:worker]
user=student
command=/usr/bin/java -jar worker.jar
directory=/home/student
EOF

service supervisor restart
