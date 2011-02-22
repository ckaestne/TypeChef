#!/bin/bash -e
#!/bin/bash -vxe

. linuxFileList.inc

##################################################################
# Preprocessing flags
##################################################################
# Hack to change and remove options just for the partial preprocessor.
. jcpp.conf

# Note: this clears $partialPreprocFlags
#partialPreprocFlags="-c linux-redhat.properties -I $(gcc -print-file-name=include) -x CONFIG_ -U __INTEL_COMPILER \
partialPreprocFlags="-c linux-$system.properties -x CONFIG_ -U __INTEL_COMPILER \
  -U __ASSEMBLY__ --include linux_defs.h --include partialConf.h --openFeat openFeaturesList.txt"
#  --include linux_defs.h --include $srcPath/include/generated/autoconf.h

# XXX: These options workaround bugs triggered by these macros.
partialPreprocFlags="$partialPreprocFlags -U CONFIG_PARAVIRT -U CONFIG_TRACE_BRANCH_PROFILING"
# Encode missing dependencies caught by the typechecker! :-D. CONFIG_SYMBOL_PREFIX must be undefined or defined to be a string.
partialPreprocFlags="$partialPreprocFlags -U CONFIG_PARAVIRT_SPINLOCKS -U CONFIG_64BIT -U CONFIG_SYMBOL_PREFIX"
# CONFIG_MACH_JAZZ is impossible in our config and causes inclusion of
# <asm/jazz.h>, not avilable for X86; it is not defined by X86, so it is not in
# the feature model. Similarly for CONFIG_SGI_HAS_I8042 and CONFIG_SNI_RM.
partialPreprocFlags="$partialPreprocFlags -U CONFIG_MACH_JAZZ -U CONFIG_SGI_HAS_I8042 -U CONFIG_SNI_RM"

# Flags which I left out from Christian configuration - they are not useful.
# partialPreprocFlags="$partialPreprocFlags -D PAGETABLE_LEVELS=4"

gccOpts="$gccOpts -nostdinc -isystem $(gcc -print-file-name=include) -include $srcPath/include/generated/autoconf.h"

flags() {
  name="$1"
  base="$(basename "$1")"
  if grep -q "arch/x86/boot" <<< "$name"; then
    extraFlag="-D_SETUP"
  else
    extraFlag=""
  fi
  # XXX: again, I need to specify $PWD, for the same bug as above.
  # "-I linux-2.6.33.3/include -I linux-2.6.33.3/arch/x86/include"
  echo "$extraFlag -I $srcPath/include -I $srcPath/arch/x86/include -D __KERNEL__ -DCONFIG_AS_CFI=1 -DCONFIG_AS_CFI_SIGNAL_FRAME=1 -DKBUILD_BASENAME=KBUILD_STR($base) -DKBUILD_MODNAME=KBUILD_STR($base) -DKBUILD_STR(s)=#s"
}

export outCSV=linux.csv
## Reset output
#echo -n > "$outCSV"

##################################################################
# Actually invoke the preprocessor and analyze result.
##################################################################
filesToProcess|while read i; do
  extraFlags="$(flags "$i")"
  . ./jcpp.sh $srcPath/$i.c $extraFlags
  . ./postProcess.sh $srcPath/$i.c $extraFlags
#  for j in $listToParse; do
#    if [ "$i" = "$j" ]; then
#      ./parseTypecheck.sh $srcPath/$i.pi
#      break
#    fi
#  done
done

# The original invocation of the compiler:
# gcc -Wp,-MD,kernel/.fork.o.d
# -nostdinc -isystem /usr/lib/gcc/x86_64-redhat-linux/4.4.4/include
# -I/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include -Iinclude
# -D__KERNEL__
# -include include/generated/autoconf.h -DCONFIG_AS_CFI=1 -DCONFIG_AS_CFI_SIGNAL_FRAME=1 -D"KBUILD_STR(s)=#s" -D"KBUILD_BASENAME=KBUILD_STR(fork)" -D"KBUILD_MODNAME=KBUILD_STR(fork)"
# -Wall -Wundef -Wstrict-prototypes -Wno-trigraphs -fno-strict-aliasing -fno-common -Werror-implicit-function-declaration -Wno-format-security -fno-delete-null-pointer-checks -O2 -m64 -mtune=generic -mno-red-zone -mcmodel=kernel -funit-at-a-time -maccumulate-outgoing-args -pipe -Wno-sign-compare -fno-asynchronous-unwind-tables -mno-sse -mno-mmx -mno-sse2 -mno-3dnow -Wframe-larger-than=2048 -fno-stack-protector -fomit-frame-pointer -Wdeclaration-after-statement -Wno-pointer-sign -fno-strict-overflow -fno-dwarf2-cfi-asm -fconserve-stack
# -c -o kernel/fork.o kernel/fork.c


# vim: set tw=0:
