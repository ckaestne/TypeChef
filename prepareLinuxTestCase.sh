#!/bin/sh -v
version=linux-2.6.33.3

#tarball=$version.tar.bz2
#if [ ! -f $tarball ]; then
#  wget http://www.kernel.org/pub/linux/kernel/v2.6/$tarball
#fi
#tar xjf $tarball

cd $version
patchesPath=../linux-2.6.33.3-patches
while read i; do
  patch -p1 -i $patchesPath/$i
done < $patchesPath/series

make allnoconfig ARCH=x86
make prepare ARCH=x86
