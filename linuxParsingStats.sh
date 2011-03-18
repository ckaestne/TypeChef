#!/bin/bash -e
. linuxFileList.inc

#XXX hack
#scalac Stats.scala

java -jar sbt-launch-0.7.4.jar "project LinuxAnalysis" "stats -f linux_files.lst linuxParse.csv"

#filesToProcess|while read i; do
#  ./parsingStats.sh $srcPath/$i.pi.dbgT linuxParse.csv
#done
