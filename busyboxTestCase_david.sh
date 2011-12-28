#!/bin/bash -e
#!/bin/bash -vxe

filesToProcess() {
  local listFile=busybox/busybox_files
  cat $listFile
  #awk -F: '$1 ~ /.c$/ {print gensub(/\.c$/, "", "", $1)}' < linux_2.6.33.3_pcs.txt
}


flags="-U HAVE_LIBDMALLOC -DCONFIG_FIND -U CONFIG_FEATURE_WGET_LONG_OPTIONS -U ENABLE_NC_110_COMPAT -U CONFIG_EXTRA_COMPAT -D_GNU_SOURCE"
srcPath="cprojects/busybox/busybox-1.18.5"
export partialPreprocFlags="-x CONFIG_ --include busybox/config.h -I $srcPath/include"
export outCSV=boa.csv
## Reset output
#echo -n > "$outCSV"
filesToProcess|while read i; do
  if [ ! -f $srcPath/$i.dbg ]; then
    touch $srcPath/$i.dbg
    ./jcpp.sh $srcPath/$i.c $flags
  else
    echo "Skipping $srcPath/$i.c"
  fi
done

