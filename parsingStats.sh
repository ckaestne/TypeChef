#!/bin/bash -e
#scala main.Stats "$@"

echo "$@"
java -jar sbt-launch-0.7.4.jar "project LinuxAnalysis" "stats $1 $2"

#bash -c "java -jar sbt-launch-0.7.4.jar \
#        'project LinuxAnalysis' 'stats $@' \
#        "
