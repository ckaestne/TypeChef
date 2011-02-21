#!/bin/bash -e
. linuxFileList.inc

#XXX hack
scalac Stats.scala
for i in $filesToProcess; do
  ./parsingStats.sh $srcPath/$i.pi.dbgT linuxParse.csv
done
