#!/bin/sh -v
version=linux-2.6.33.3

wget http://www.kernel.org/pub/linux/kernel/v2.6/$version.tar.bz2
tar xjf $version.tar.bz2
cd $version
while read i; do
  patch -p1 -i $i
done < linux-2.6.33.3-patches/series

make allnoconfig ARCH=x86
make prepare ARCH=x86
