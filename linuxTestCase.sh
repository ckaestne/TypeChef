#!/bin/bash -vxe
#!/bin/bash -e
# Hack to add an option just for the partial preprocessor.
. jcpp.conf

partialPreprocFlags="$partialPreprocFlags -x CONFIG_ -U __INTEL_COMPILER \
  -U __ASSEMBLY__"
# I don't know what to do with these flags. They should not be here!
partialPreprocFlags="$partialPreprocFlags -D PAGETABLE_LEVELS=4 \
  -U CONFIG_PARAVIRT -D CONFIG_HZ=100 -D __KERNEL__"

flags="-I linux-2.6.33.3/include -I linux-2.6.33.3/arch/x86/include"
srcPath=linux-2.6.33.3
flags="-I $PWD/linux-2.6.33.3/include -I $PWD/linux-2.6.33.3/arch/x86/include"
srcPath=$PWD/linux-2.6.33.3
list=kernel/fork

for i in $list; do
  ./jcpp.sh $srcPath/$i.c $flags
done
for i in $list; do
  ./postProcess.sh $srcPath/$i.c $flags
done
