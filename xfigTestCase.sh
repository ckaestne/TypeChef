#!/bin/bash
#!/bin/bash -vxe

# after configure run make because sqlite3.h is created from sqlite3.h.in

srcPath=./casestudies/xfig.3.2.5
prjPath=./casestudies/xfig.3.2.5
export partialPreprocFlags='-p true
	-U SVR4
	-U __SVR4
	-U CSRG_BASED
	-U __NetBSD__
	-U __OpenBSD__
'
flags='
	-I '$prjPath'
	-I '$srcPath'
	--include host/platform.h
	-I /usr/local/include
	-I /usr/lib/x86_64-linux-gnu/gcc/x86_64-linux-gnu/4.5.2/include
	-I /usr/lib/x86_64-linux-gnu/gcc/x86_64-linux-gnu/4.5.2/include-fixed
	-I /usr/include/x86_64-linux-gnu
	-I /usr/include
'

##################################################################
# Actually invoke the preprocessor and analyze result.
##################################################################

for i in `find "$srcPath" -type f -name "*.c"`;
do
    ./jcpp.sh $i $flags
done

