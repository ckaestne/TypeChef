#!/bin/bash
#!/bin/bash -vxe

# after configure run make because sqlite3.h is created from sqlite3.h.in

srcPath=./casestudies/sqlite-3.6.10/src
prjPath=./casestudies/sqlite-3.6.10
export partialPreprocFlags="-p false \
	-U HAVE_LOCALTIME_S\
	-U _FILE_OFFSET_BITS\
	-U _LARGE_FILES\
	--openFeat $prjPath/sqlitefeatures.txt\
"
flags="\
	-I $prjPath -I $srcPath\
	--include ./host/platform.h\
	-I /usr/local/include\
	-I /usr/lib/x86_64-linux-gnu/gcc/x86_64-linux-gnu/4.5.2/include\
	-I /usr/lib/x86_64-linux-gnu/gcc/x86_64-linux-gnu/4.5.2/include-fixed\
	-I /usr/include/x86_64-linux-gnu\
	-I /usr/include\
"

##################################################################
# Actually invoke the preprocessor and analyze result.
##################################################################

for i in `find "$srcPath" -type f -name "*.c"`;
do
    ./jcpp.sh $i $flags
done

for i in `find "$srcPath" -type f -name "*.h"`;
do
    ./jcpp.sh $i $flags
done

