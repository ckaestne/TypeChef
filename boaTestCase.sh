#!/bin/bash
#!/bin/bash -vxe

# lighttpd configured with
# ./configure --without-pcre --without-bzip2
# grep '#undef' config.h -> -U flags for partialPreprocFlags
# grep '#define' config.h -> modified version goes into lighttpdfeatures.txt

srcPath=./cprojects/boa/boa-0.94.13/src
prjPath=./cprojects/boa/boa-0.94.13
export partialPreprocFlags=""
# export partialPreprocFlags="-p false\
# 	--openFeat $prjPath/lighttpdfeatures.txt"
flags="\
	-I $prjPath -I $srcPath\
	--include ./host/platform.h\
	--include $srcPath/config.h\
	-I /usr/local/include\
	-I /usr/lib/x86_64-linux-gnu/gcc/x86_64-linux-gnu/4.5.2/include\
	-I /usr/lib/x86_64-linux-gnu/gcc/x86_64-linux-gnu/4.5.2/include-fixed\
	-I /usr/include/x86_64-linux-gnu\
	-I /usr/include/"

##################################################################
# Actually invoke the preprocessor and analyze result.
##################################################################

./jcpp.sh $srcPath/hash.c

exit

for i in `find "$srcPath" -type f -name "*.c"`;
do
    ./jcpp.sh $i $flags
done

for i in `find "$srcPath" -type f -name "*.h"`;
do
    ./jcpp.sh $i $flags
done
