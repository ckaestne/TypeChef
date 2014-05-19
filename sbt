#!/bin/sh
java -XX:PermSize=128m -XX:MaxPermSize=256m -Xmx2048m -Xms128m -Xss10m -jar sbt-launch.jar "$@"
