#!/bin/bash -e
#!/bin/bash -vxe

srcPath=sparse

# Hack to add an option just for the partial preprocessor.
. jcpp.conf

partialPreprocFlags="$partialPreprocFlags -P _H -p _"
#partialPreprocFlags="$partialPreprocFlags -c gtk.properties -P _H"

flags="$(pkg-config --cflags gtk+-2.0) $(pkg-config --cflags libxml-2.0)"

fileList=""

for i in $srcPath/*.c; do
  case $i in
    sparse/compat-mingw.c|sparse/compat-solaris.c)
      #Skip these, they don't compile here.
      ;;
    *)
      fileList="$fileList $i"
      ;;
  esac
done

export outCSV=sparse.csv
## Reset output
#echo -n > "$outCSV"

for i in $fileList; do
  . ./jcpp.sh $i $flags
done
for i in $fileList; do
  . ./postProcess.sh $i $flags
done
