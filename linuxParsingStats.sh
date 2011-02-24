#!/bin/bash -e
. linuxFileList.inc

#XXX hack
scalac Stats.scala
filesToProcess|while read i; do
  ./parsingStats.sh $srcPath/$i.pi.dbgT linuxParse.csv
done
