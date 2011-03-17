#!/bin/bash -e
. linuxFileList.inc

filesToProcess|while read i; do
  if [ -f $srcPath/$i.pi ]; then
      if [ ! -f $srcPath/$i.pi.dbgT ]; then
        touch $srcPath/$i.pi.dbgT
        ./parseTypecheck.sh $srcPath/$i.pi
      else
        echo "Skipping $srcPath/$i.pi"
      fi
  else
    echo "File $srcPath/$i.pi not found."
  fi
done
