#!/bin/bash -e
#!/bin/bash -vxe

flags="-U HAVE_LIBDMALLOC -DCONFIG_FIND -U CONFIG_FEATURE_WGET_LONG_OPTIONS -U ENABLE_NC_110_COMPAT -U CONFIG_EXTRA_COMPAT -D_GNU_SOURCE"
./typechef.sh \
	--bdd \
	-x CONFIG_ \
	--featureModelDimacs bb.dimacs \
	--writePI \
	--recordTiming \
	--parserstatistics \
	--analysis liveness \
	$flags \
	tar.c

