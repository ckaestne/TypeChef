#!/bin/bash -e
#!/bin/bash -vxe

preprocess() {
  #TODO: use arguments not global vars!
  #flags="$1"
  #fileList="$2"
  for i in $fileList; do
    ./jcpp.sh $dir/$i.c $flags $folderFlags
  done
  for i in $fileList; do
    ./postProcess.sh $dir/$i.c $flags $folderFlags
  done
}

srcPath=$PWD/http-2.2.17

export partialPreprocFlags="-P _H -p _"

flags="-DHAVE_CONFIG_H -DLINUX=2 -D_REENTRANT -D_GNU_SOURCE"

subdir=srclib/apr
fileList="passwd/apr_getpass.c strings/apr_cpystrn.c strings/apr_strtok.c strings/apr_snprintf.c strings/apr_strnatcmp.c strings/apr_fnmatch.c strings/apr_strings.c tables/apr_tables.c tables/apr_hash.c dso/unix/dso.c file_io/unix/mktemp.c file_io/unix/seek.c file_io/unix/copy.c file_io/unix/filedup.c file_io/unix/dir.c file_io/unix/flock.c file_io/unix/buffer.c file_io/unix/filepath_util.c file_io/unix/readwrite.c file_io/unix/open.c file_io/unix/fileacc.c file_io/unix/tempdir.c file_io/unix/pipe.c file_io/unix/filepath.c file_io/unix/filestat.c file_io/unix/fullrw.c locks/unix/thread_rwlock.c locks/unix/thread_mutex.c locks/unix/global_mutex.c locks/unix/proc_mutex.c locks/unix/thread_cond.c memory/unix/apr_pools.c misc/unix/rand.c misc/unix/start.c misc/unix/otherchild.c misc/unix/getopt.c misc/unix/env.c misc/unix/version.c misc/unix/charset.c misc/unix/errorcodes.c mmap/unix/common.c mmap/unix/mmap.c network_io/unix/socket_util.c network_io/unix/inet_ntop.c network_io/unix/inet_pton.c network_io/unix/sockets.c network_io/unix/sockaddr.c network_io/unix/multicast.c network_io/unix/sockopt.c network_io/unix/sendrecv.c poll/unix/pollcb.c poll/unix/port.c poll/unix/select.c poll/unix/epoll.c poll/unix/pollset.c poll/unix/kqueue.c poll/unix/poll.c random/unix/apr_random.c random/unix/sha2.c random/unix/sha2_glue.c shmem/unix/shm.c support/unix/waitio.c threadproc/unix/procsup.c threadproc/unix/threadpriv.c threadproc/unix/proc.c threadproc/unix/thread.c threadproc/unix/signals.c time/unix/timestr.c time/unix/time.c user/unix/userinfo.c user/unix/groupinfo.c atomic/unix/mutex.c atomic/unix/solaris.c atomic/unix/ia32.c atomic/unix/s390.c atomic/unix/builtins.c atomic/unix/ppc.c"

dir=$srcPath/$subdir
folderFlags="-I$dir/include -I$srcPath/srclib/apr/include/arch/unix -I$dir/include/arch/unix -I$srcPath/srclib/apr/include/arch/unix -I$srcPath/srclib/apr/include"

preprocess

subdir=srclib/apr-util/xml/expat
fileList="lib/xmlparse.c lib/xmltok.c lib/xmlrole.c"

dir=$srcPath/$subdir
flags="-DHAVE_EXPAT_CONFIG_H   -I$dir/lib -I$dir"
folderFlags=""

preprocess

# I commented out these other compilation flags:
# -O2 # This influences the _OPTIMIZE_ macro (check the exact spelling)

