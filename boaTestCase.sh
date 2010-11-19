#!/bin/bash -e
#!/bin/bash -vxe
list="alias boa buffer cgi cgi_header config escape get hash ip log mmap_cache pipe queue read request response select signals sublog util"
flags="-U HAVE_LIBDMALLOC -p _"
srcPath=boa/src

export outCSV=boa.csv
## Reset output
#echo -n > "$outCSV"

for i in $list; do
  ./jcpp.sh $srcPath/$i.c $flags
done
for i in $list; do
  ./postProcess.sh $srcPath/$i.c $flags
done
