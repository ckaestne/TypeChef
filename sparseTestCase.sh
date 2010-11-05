#!/bin/bash -e

# Hack to add an option just for the partial preprocessor.
. jcpp.conf

partialPreprocFlags="$partialPreprocFlags -P _H"
#partialPreprocFlags="$partialPreprocFlags -c gtk.properties -P _H"
## Hmm... do we need to customize gtk.properties depending on the host system, to
## match the output of pkg-config? Yes!
## But actually, we can just pass pkg-config output to our preprocessor!
#gccOpts="$gccOpts $(pkg-config --cflags gtk+-2.0) $(pkg-config --cflags libxml-2.0)"

for i in sparse/*.c; do
  case $i in
    sparse/compat-mingw.c|sparse/compat-solaris.c)
      #Skip these, they don't compile here.
      ;;
    *)
      . ./jcpp.sh $i $(pkg-config --cflags gtk+-2.0) $(pkg-config --cflags libxml-2.0)
      ;;
  esac
done
