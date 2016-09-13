#!/bin/bash
adduser --disabled-password --gecos "" student
cd /home/student

TOKEN=`curl http://metadata/computeMetadata/v1/instance/service-accounts/default/token -H "X-Google-Metadata-Request: True" |  sed -nr 's/^.*"access_token":\s*"(.*)",.*$/\1/p'`

wget --header "Authorization: OAuth $TOKEN" umass-compsci220.storage.googleapis.com/distgrader.jar

DOCKER_HOST="localhost:2376" gcloud docker pull gcr.io/arjun-umass/grading-compsci220

cat << EOF > /etc/supervisor/conf.d/supervisord.conf
[supervisord]
nodaemon=false

[program:worker]
user=student
command=/usr/bin/java -jar distgrader.jar worker
directory=/home/student
stdout_logfile=syslog
stderr_logfile=syslog
EOF

service supervisor restart
