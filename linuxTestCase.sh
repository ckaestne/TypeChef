#!/bin/bash -e
#!/bin/bash -vxe

##################################################################
# Location of the Linux kernel.
##################################################################
#srcPath=linux-2.6.33.3
# XXX:$PWD/ makes the path absolute, it is needed for some stupid bug!
srcPath=$PWD/linux-2.6.33.3

##################################################################
# List of files to preprocess
##################################################################
# Excluded:
# arch/x86/kernel/process_64 contains a real error in this configuration, in which it cannot be compiled.
# kernel/timer - unsupported include FOO(bar) construct
# arch/x86/kernel/traps - strange preprocessor bug, not always reproducible, I'm really confused; takes half an hour anyway.
# arch/x86/kernel/time - takes half an hour.
# Processed:
list="init/calibrate init/main arch/x86/kernel/signal"
list="$list kernel/fork drivers/video/console/dummycon"
list="$list arch/x86/kernel/irq arch/x86/kernel/irq_64 arch/x86/kernel/dumpstack_64 arch/x86/kernel/ioport arch/x86/kernel/ldt arch/x86/kernel/dumpstack arch/x86/kernel/setup"
list="$list lib/proportions lib/prio_tree lib/find_next_bit mm/filemap mm/oom_kill"
list="$list kernel/kprobes kernel/exit"
#Problematic
list="$list arch/x86/kernel/traps arch/x86/kernel/time"

# x86 architecture - we don't want to use all of this for the evaluation, but to have a more varied setup.
#list="$list arch/x86/kernel/process_64 arch/x86/kernel/signal arch/x86/kernel/traps arch/x86/kernel/irq arch/x86/kernel/irq_64 arch/x86/kernel/dumpstack_64 arch/x86/kernel/time arch/x86/kernel/ioport arch/x86/kernel/ldt arch/x86/kernel/dumpstack arch/x86/kernel/setup arch/x86/kernel/x86_init arch/x86/kernel/i8259 arch/x86/kernel/irqinit arch/x86/kernel/sys_x86_64 arch/x86/kernel/x8664_ksyms_64 arch/x86/kernel/syscall_64 arch/x86/kernel/vsyscall_64 arch/x86/kernel/bootflag arch/x86/kernel/e820 arch/x86/kernel/quirks arch/x86/kernel/i8237 arch/x86/kernel/topology arch/x86/kernel/kdebugfs arch/x86/kernel/alternative arch/x86/kernel/i8253 arch/x86/kernel/hw_breakpoint arch/x86/kernel/tsc arch/x86/kernel/io_delay arch/x86/kernel/rtc arch/x86/kernel/process arch/x86/kernel/i387 arch/x86/kernel/xsave arch/x86/kernel/ptrace arch/x86/kernel/step arch/x86/kernel/reboot arch/x86/kernel/mpparse arch/x86/kernel/early_printk arch/x86/kernel/hpet arch/x86/kernel/pcspeaker arch/x86/kernel/vsmp_64 arch/x86/kernel/head64 arch/x86/kernel/head arch/x86/kernel/init_task"
list="$list arch/x86/kernel/x86_init arch/x86/kernel/i8259 arch/x86/kernel/irqinit arch/x86/kernel/sys_x86_64 arch/x86/kernel/x8664_ksyms_64 arch/x86/kernel/syscall_64 arch/x86/kernel/vsyscall_64 arch/x86/kernel/bootflag arch/x86/kernel/e820 arch/x86/kernel/quirks arch/x86/kernel/i8237 arch/x86/kernel/topology arch/x86/kernel/kdebugfs arch/x86/kernel/alternative arch/x86/kernel/i8253 arch/x86/kernel/hw_breakpoint arch/x86/kernel/tsc arch/x86/kernel/io_delay arch/x86/kernel/rtc arch/x86/kernel/process arch/x86/kernel/i387 arch/x86/kernel/xsave arch/x86/kernel/ptrace arch/x86/kernel/step arch/x86/kernel/reboot arch/x86/kernel/mpparse arch/x86/kernel/early_printk arch/x86/kernel/hpet arch/x86/kernel/pcspeaker arch/x86/kernel/vsmp_64 arch/x86/kernel/head64 arch/x86/kernel/head arch/x86/kernel/init_task"
##################################################################
# Preprocessing flags
##################################################################
# Hack to change and remove options just for the partial preprocessor.
. jcpp.conf

# Note: this clears $partialPreprocFlags
#partialPreprocFlags="-c linux-redhat.properties -I $(gcc -print-file-name=include) -x CONFIG_ -U __INTEL_COMPILER \
partialPreprocFlags="-c linux-redhat.properties -x CONFIG_ -U __INTEL_COMPILER \
  -U __ASSEMBLY__ --include $srcPath/include/generated/autoconf.h"
# I don't know what to do with these flags. They should not be here!
# partialPreprocFlags="$partialPreprocFlags " "-D PAGETABLE_LEVELS=4 -D CONFIG_HZ=100"

# XXX: These options workaround bugs triggered by these macros.
partialPreprocFlags="$partialPreprocFlags -U CONFIG_PARAVIRT -U CONFIG_TRACE_BRANCH_PROFILING"

# Flags which I left out from Christian configuration - they are not useful.
# partialPreprocFlags="$partialPreprocFlags -D PAGETABLE_LEVELS=4 -D CONFIG_HZ=100"

gccOpts="$gccOpts -nostdinc -isystem $(gcc -print-file-name=include) -include $srcPath/include/generated/autoconf.h"

flags() {
  base="$1"
  # XXX: again, I need to specify $PWD, for the same bug as above.
  # "-I linux-2.6.33.3/include -I linux-2.6.33.3/arch/x86/include"
  echo "-I $srcPath/include -I $srcPath/arch/x86/include -D __KERNEL__ -DCONFIG_AS_CFI=1 -DCONFIG_AS_CFI_SIGNAL_FRAME=1 -DKBUILD_BASENAME=KBUILD_STR($base) -DKBUILD_MODNAME=KBUILD_STR($base) -DKBUILD_STR(s)=#s"
}

export outCSV=linux.csv
## Reset output
#echo -n > "$outCSV"

##################################################################
# Actually invoke the preprocessor and analyze result.
##################################################################
for i in $list; do
  base=$(basename $i)
  . ./jcpp.sh $srcPath/$i.c $(flags "$base")
  . ./postProcess.sh $srcPath/$i.c $(flags "$base")
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
