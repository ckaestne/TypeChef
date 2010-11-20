#!/bin/sh -v
version=linux-2.6.33.3

wget http://www.kernel.org/pub/linux/kernel/v2.6/$version.tar.bz2
tar xjf $version.tar.bz2
cd $version

make allnoconfig ARCH=x86
make prepare ARCH=x86
