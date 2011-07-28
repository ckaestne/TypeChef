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
"

##################################################################
# Actually invoke the preprocessor and analyze result.
##################################################################


for i in `find "$srcPath" -type f -name "*.c"`;
do
    ./jcpp.sh $i $flags
done

