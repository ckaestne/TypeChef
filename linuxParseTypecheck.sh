#!/bin/bash -e
. linuxFileList.inc

filesToProcess|while read i; do
  if [ ! -f $srcPath/$i.pi.dbgT ]; then
    touch $srcPath/$i.pi.dbgT
    ./parseTypecheck.sh $srcPath/$i.pi
  else
    echo "Skipping $srcPath/$i.pi"
  fi
done
