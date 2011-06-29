#!/bin/bash
#!/bin/bash -vxe

# lighttpd configured with
# ./configure --without-pcre --without-bzip2
# grep '#undef' config.h -> -U flags for partialPreprocFlags
# grep '#define' config.h -> modified version goes into lighttpdfeatures.txt

srcPath=./casestudies/lighttpd-1.4.22/src
prjPath=./casestudies/lighttpd-1.4.22
export partialPreprocFlags="-p false\
	-U HAVE_ATTR_ATTRIBUTES_H\
	-U HAVE_BZLIB_H\
	-U HAVE_ERRMSG_H\
	-U HAVE_FAMNOEXISTS\
	-U HAVE_FAM_H\
	-U HAVE_FASTCGI_FASTCGI_H\
	-U HAVE_FASTCGI_H\
	-U HAVE_GDBM\
	-U HAVE_GDBM_H\
	-U HAVE_KQUEUE\
	-U HAVE_LBER_H\
	-U HAVE_LDAP_H\
	-U HAVE_LIBBZ2\
	-U HAVE_LIBFAM\
	-U HAVE_LIBLBER\
	-U HAVE_LIBLDAP\
	-U HAVE_LIBPCRE\
	-U HAVE_LIBSSL\
	-U HAVE_LIBXML2\
	-U HAVE_LIBXML_H\
	-U HAVE_LUA\
	-U HAVE_LUA_H\
	-U HAVE_MEMCACHE\
	-U HAVE_MEMCACHE_H\
	-U HAVE_MYSQL\
	-U HAVE_MYSQL_H\
	-U HAVE_OPENSSL_SSL_H\
	-U HAVE_PCRE_H\
	-U HAVE_PORT_CREATE\
	-U HAVE_SENDFILEV\
	-U HAVE_SENDFILE_BROKEN\
	-U HAVE_SEND_FILE\
	-U HAVE_SQLITE3\
	-U HAVE_SQLITE3_H\
	-U HAVE_STAT_EMPTY_STRING_BUG\
	-U HAVE_SYS_DEVPOLL_H\
	-U HAVE_SYS_EVENT_H\
	-U HAVE_SYS_FILIO_H\
	-U HAVE_SYS_PORT_H\
	-U HAVE_SYS_SYSLIMITS_H\
	-U HAVE_UUID\
	-U HAVE_UUID_H\
	-U HAVE_UUID_UUID_H\
	-U HAVE_VALGRIND_VALGRIND_H\
	-U HAVE_VFORK_H\
	-U HAVE_XATTR\
	-U LDAP_DEPRECATED\
	-U NO_MINUS_C_MINUS_O\
	-U _MINIX\
	-U _POSIX_1_SOURCE\
	-U _POSIX_SOURCE\
	-U const\
	-U inline\
	-U off_t\
	-U pid_t\
	-U size_t\
	-U vfork\
	--openFeat $prjPath/lighttpdfeatures.txt"
flags="\
	-I $prjPath -I $srcPath\
	--include ./host/platform.h\
	-I /usr/local/include\
	-I /usr/lib/x86_64-linux-gnu/gcc/x86_64-linux-gnu/4.5.2/include\
	-I /usr/lib/x86_64-linux-gnu/gcc/x86_64-linux-gnu/4.5.2/include-fixed\
	-I /usr/include/x86_64-linux-gnu\
	-I /usr/include/"

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
