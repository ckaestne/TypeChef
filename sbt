#!/bin/sh
SBT_OPTS="-XX:PermSize=512M -XX:MaxPermSize=512M"
exec java -Xmx2048M ${SBT_OPTS} -jar ./sbt-launch.jar "$@"
