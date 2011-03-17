#!/bin/bash -e
#scala main.Stats "$@"


java -jar sbt-launch-0.7.4.jar "project LinuxAnalysis" "stats $@"

#bash -c "java -jar sbt-launch-0.7.4.jar \
#        'project LinuxAnalysis' 'stats $@' \
#        "
