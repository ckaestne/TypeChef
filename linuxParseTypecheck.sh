#!/bin/bash -e
. linuxFileList.inc

for i in $filesToProcess; do
  ./parseTypecheck.sh $srcPath/$i.pi
done
