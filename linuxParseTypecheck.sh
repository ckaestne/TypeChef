#!/bin/bash -e
. linuxFileList.inc

filesToProcess|while read i; do
  ./parseTypecheck.sh $srcPath/$i.pi
done
