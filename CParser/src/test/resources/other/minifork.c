#line 1 "host/platform-redhat-gnu89-O2.h" 1
//echo -|gcc -dM -x c - -E -nostdinc -isystem /usr/lib/gcc/x86_64-redhat-linux/4.4.4/include -O2 





































































































































#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/generated/autoconf.h" 1
/*
 * Automatically generated C config: don't edit
 * Linux kernel version: 2.6.33.3
 * Fri Nov 19 19:51:09 2010
 */























































































































































































































#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/kernel/fork.c" 1
/*
 *  linux/kernel/fork.c
 *
 *  Copyright (C) 1991, 1992  Linus Torvalds
 */

/*
 *  'fork.c' contains the help-routines for the 'fork' system call
 * (see also entry.S and others).
 * Fork is rather simple, once you get the hang of it, but the memory
 * management can be a bitch. See 'mm/memory.c': 'copy_page_range()'
 */

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/slab.h" 1
/*
 * Written by Mark Hemment, 1996 (markhe@nextd.demon.co.uk).
 *
 * (C) SGI 2006, Christoph Lameter
 * 	Cleaned up and restructured to ease the addition of alternative
 * 	implementations of SLAB allocators.
 */




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/gfp.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mmzone.h" 1





#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/spinlock.h" 1



/*
 * include/linux/spinlock.h - generic spinlock/rwlock declarations
 *
 * here's the role of the various spinlock/rwlock related include files:
 *
 * on SMP builds:
 *
 *  asm/spinlock_types.h: contains the arch_spinlock_t/arch_rwlock_t and the
 *                        initializers
 *
 *  linux/spinlock_types.h:
 *                        defines the generic type and initializers
 *
 *  asm/spinlock.h:       contains the arch_spin_*()/etc. lowlevel
 *                        implementations, mostly inline assembly code
 *
 *   (also included on UP-debug builds:)
 *
 *  linux/spinlock_api_smp.h:
 *                        contains the prototypes for the _spin_*() APIs.
 *
 *  linux/spinlock.h:     builds the final spin_*() APIs.
 *
 * on UP builds:
 *
 *  linux/spinlock_type_up.h:
 *                        contains the generic, simplified UP spinlock type.
 *                        (which is an empty structure on non-debug builds)
 *
 *  linux/spinlock_types.h:
 *                        defines the generic type and initializers
 *
 *  linux/spinlock_up.h:
 *                        contains the arch_spin_*()/etc. version of UP
 *                        builds. (which are NOPs on non-debug, non-preempt
 *                        builds)
 *
 *   (included on UP-non-debug builds:)
 *
 *  linux/spinlock_api_up.h:
 *                        builds the _spin_*() APIs.
 *
 *  linux/spinlock.h:     builds the final spin_*() APIs.
 */

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/typecheck.h" 1



/*
 * Check at compile time that something is of a particular type.
 * Always evaluates to 1 so you may use it easily in comparisons.
 */







/*
 * Check at compile time that 'function' is a certain type, or is a pointer
 * to that type (needs to use typedef for the function type.)
 */






#line 51 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/spinlock.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/preempt.h" 1



/*
 * include/linux/preempt.h - macros for accessing and manipulating
 * preempt_count (used for kernel preemption, interrupt count, etc.)
 */

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/thread_info.h" 1
/* thread_info.h: common low-level thread information accessors
 *
 * Copyright (C) 2002  David Howells (dhowells@redhat.com)
 * - Incorporating suggestions made by Linus Torvalds
 */




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/types.h" 1





#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/types.h" 1


/*
 * int-ll64 is used practically everywhere now,
 * so use it as a reasonable default.
 */
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/int-ll64.h" 1
/*
 * asm-generic/int-ll64.h
 *
 * Integer declarations for architectures which use "long long"
 * for 64-bit types.
 */




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bitsperlong.h" 1





 

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitsperlong.h" 1



/*
 * There seems to be no way of detecting this automatically from user
 * space, so 64 bit architectures should override this in their
 * bitsperlong.h. In particular, an architecture that supports
 * both 32 and 64 bit user space must not rely on CONFIG_64BIT
 * to decide it, but rather check a compiler provided macro.
 */




#if definedEx(CONFIG_64BIT)

#endif
#if !(definedEx(CONFIG_64BIT))

#endif
/*
 * FIXME: The check currently breaks x86-64 build, so it's
 * temporarily disabled. Please fix x86-64 and reenable
 */





#line 12 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bitsperlong.h" 2


#line 13 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/int-ll64.h" 2


/*
 * __xx is ok: it doesn't pollute the POSIX namespace. Use these in the
 * header files exported to user space
 */

typedef __signed__ char __s8;
typedef unsigned char __u8;

typedef __signed__ short __s16;
typedef unsigned short __u16;

typedef __signed__ int __s32;
typedef unsigned int __u32;


__extension__ typedef __signed__ long long __s64;
__extension__ typedef unsigned long long __u64;
 





typedef signed char s8;
typedef unsigned char u8;

typedef signed short s16;
typedef unsigned short u16;

typedef signed int s32;
typedef unsigned int u32;

typedef signed long long s64;
typedef unsigned long long u64;










 











#line 9 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/types.h" 2


typedef unsigned short umode_t;


/*
 * These aren't exported outside the kernel to avoid name space clashes
 */


/*
 * DMA addresses may be very different from physical addresses
 * and pointers. i386 and powerpc may have 64 bit DMA on 32 bit
 * systems, while sparc64 uses 32 bit DMA addresses for 64 bit
 * physical addresses.
 * This default defines dma_addr_t to have the same size as
 * phys_addr_t, which is the most common way.
 * Do not define the dma64_addr_t type, which never really
 * worked.
 */



 





#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/types.h" 2



typedef u64 dma64_addr_t;
#if (definedEx(CONFIG_X86_64) || definedEx(CONFIG_HIGHMEM64G))
/* DMA addresses come in 32-bit and 64-bit flavours. */
typedef u64 dma_addr_t;
#endif
#if !((definedEx(CONFIG_X86_64) || definedEx(CONFIG_HIGHMEM64G)))
typedef u32 dma_addr_t;
#endif



#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 2







#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/posix_types.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/stddef.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















#if 1















#endif


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler-gcc.h" 1



/*
 * Common definitions for all gcc versions go here.
 */


/* Optimization barrier */
/* The "volatile" is due to gcc bugs */


/*
 * This macro obfuscates arithmetic on a variable address so that gcc
 * shouldn't recognize the original var, and make assumptions about it.
 *
 * This is needed because the C standard makes it undefined to do
 * pointer arithmetic on "objects" outside their boundaries and the
 * gcc optimizers assume this is the case. In particular they
 * assume such arithmetic does not wrap.
 *
 * A miscompilation has been observed because of this on PPC.
 * To work around it we hide the relationship of the pointer and the object
 * using this macro.
 *
 * Versions of the ppc64 compiler before 4.1 had a bug where use of
 * RELOC_HIDE could trash r30. The bug can be worked around by changing
 * the inline assembly constraint from =g to =r, in this particular
 * case either is valid.
 */





/* &a[0] degrades to a pointer: a different type from an array */



/*
 * Force always-inline if the user requests it so via the .config,
 * or if gcc is too old:
 */
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))



#endif




/*
 * it doesn't make sense on ARM (currently the only user of __naked) to trace
 * naked functions because then mcount is called without stack and frame pointer
 * being set up and there is no chance to restore the lr register to the value
 * before mcount was called.
 */




/*
 * From the GCC manual:
 *
 * Many functions have no effects except the return value and their
 * return value depends only on the parameters and/or global
 * variables.  Such a function can be subject to common subexpression
 * elimination and loop optimization just as an arithmetic operator
 * would be.
 * [...]
 */











//ChK: #include gcc_header(__GNUC__)
//
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler-gcc3.h" 1








 




#if definedEx(CONFIG_GCOV_KERNEL)



#endif
/*
 * A trick to suppress uninitialized variable warning without generating any
 * code
 */



#line 91 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler-gcc.h" 2
#line 44 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 2



/* Intel compiler defines __GNUC__. So we will overwrite implementations
 * coming from above header files here
 */



/*
 * Generic compiler-dependent macros required for kernel
 * build go below this comment. Actual compiler/compiler version
 * specific implementations come from the above header files
 */

struct ftrace_branch_data {
	const char *func;
	const char *file;
	unsigned line;
	union {
		struct {
			unsigned long correct;
			unsigned long incorrect;
		};
		struct {
			unsigned long miss;
			unsigned long hit;
		};
		unsigned long miss_hit[2];
	};
};

/*
 * Note: DISABLE_BRANCH_PROFILING can be used by special lowlevel code
 * to disable branch tracing on a per file basis.
 */























































#if 1


#endif
/* Optimization barrier */



/* Unreachable code */












/*
 * Allow us to mark functions as 'deprecated' and have gcc emit a nice
 * warning for each use, in hopes of speeding the functions removal.
 * Usage is:
 * 		int __deprecated foo(void)
 */





#if 1

#endif



#if !(definedEx(CONFIG_ENABLE_MUST_CHECK))


#endif
#if !(definedEx(CONFIG_ENABLE_WARN_DEPRECATED))




#endif
/*
 * Allow us to avoid 'defined but not used' warnings on functions and data,
 * as well as force them to be emitted to the assembly file.
 *
 * As of gcc 3.4, static functions that are not marked with attribute((used))
 * may be elided from the assembly file.  As of gcc 3.4, static data not so
 * marked will not be elided, but this may change in a future gcc version.
 *
 * NOTE: Because distributions shipped with a backported unit-at-a-time
 * compiler in gcc 3.3, we must define __used to be __attribute__((used))
 * for gcc >=3.3 instead of 3.4.
 *
 * In prior versions of gcc, such functions and data would be emitted, but
 * would be warned about except with attribute((unused)).
 *
 * Mark functions that are referenced only in inline assembly as __used so
 * the code is emitted even though it appears to be unreferenced.
 */












/*
 * Rather then using noinline to prevent stack consumption, use
 * noinline_for_stack instead.  For documentaiton reasons.
 */






/*
 * From the GCC manual:
 *
 * Many functions do not examine any values except their arguments,
 * and have no effects except the return value.  Basically this is
 * just slightly more strict class than the `pure' attribute above,
 * since function is not allowed to read global memory.
 *
 * Note that a function that has pointer arguments and examines the
 * data pointed to must _not_ be declared `const'.  Likewise, a
 * function that calls a non-`const' function usually must not be
 * `const'.  It does not make sense for a `const' function to return
 * `void'.
 */



/*
 * Tell gcc if a function is cold. The compiler will assume any path
 * directly leading to the call is unlikely.
 */




/* Simple shorthand for a section definition */



/* Are two types/vars the same type (ignoring qualifiers)? */



/* Compile time object size, -1 for unknown */









/*
 * Prevent the compiler from merging or refetching accesses.  The compiler
 * is also forbidden from reordering successive instances of ACCESS_ONCE(),
 * but only when the compiler is aware of some particular ordering.  One way
 * to make the compiler aware of ordering is to put the two invocations of
 * ACCESS_ONCE() in different C statements.
 *
 * This macro does absolutely -nothing- to prevent the CPU from reordering,
 * merging, or refetching absolutely anything at any time.  Its main intended
 * use is to mediate communication between process-level code and irq/NMI
 * handlers, all running on the same CPU.
 */



#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/stddef.h" 2




#if 1

#endif

enum {
	false	= 0,
	true	= 1
};




#if 1

#endif


#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/posix_types.h" 2

/*
 * This allows for 1024 file descriptors: if NR_OPEN is ever grown
 * beyond that you'll have to change this too. But 1024 fd's seem to be
 * enough even for such "real" unices like OSF/1, so hopefully this is
 * one limit that doesn't have to be changed [again].
 *
 * Note that POSIX wants the FD_CLEAR(fd,fdsetp) defines to be in
 * <sys/time.h> (and thus <linux/time.h>) - but this is a more logical
 * place for them. Solved by having dummy defines in <sys/time.h>.
 */

/*
 * Those macros may have been defined in <gnu/types.h>. But we always
 * use the ones here. 
 */















typedef struct {
	unsigned long fds_bits [(1024/(8 * sizeof(unsigned long)))];
} __kernel_fd_set;

/* Type of a signal handler.  */
typedef void (*__kernel_sighandler_t)(int);

/* Type of a SYSV IPC key.  */
typedef int __kernel_key_t;
typedef int __kernel_mqd_t;

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/posix_types.h" 1


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/posix_types_32.h" 1



/*
 * This file is generally used by user-level software, so you need to
 * be a little careful about namespace pollution etc.  Also, we cannot
 * assume GCC is being used.
 */

typedef unsigned long	__kernel_ino_t;
typedef unsigned short	__kernel_mode_t;
typedef unsigned short	__kernel_nlink_t;
typedef long		__kernel_off_t;
typedef int		__kernel_pid_t;
typedef unsigned short	__kernel_ipc_pid_t;
typedef unsigned short	__kernel_uid_t;
typedef unsigned short	__kernel_gid_t;
typedef unsigned int	__kernel_size_t;
typedef int		__kernel_ssize_t;
typedef int		__kernel_ptrdiff_t;
typedef long		__kernel_time_t;
typedef long		__kernel_suseconds_t;
typedef long		__kernel_clock_t;
typedef int		__kernel_timer_t;
typedef int		__kernel_clockid_t;
typedef int		__kernel_daddr_t;
typedef char *		__kernel_caddr_t;
typedef unsigned short	__kernel_uid16_t;
typedef unsigned short	__kernel_gid16_t;
typedef unsigned int	__kernel_uid32_t;
typedef unsigned int	__kernel_gid32_t;

typedef unsigned short	__kernel_old_uid_t;
typedef unsigned short	__kernel_old_gid_t;
typedef unsigned short	__kernel_old_dev_t;


typedef long long	__kernel_loff_t;

typedef struct {
	int	val[2];
} __kernel_fsid_t;








































#line 5 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/posix_types.h" 2
 

 

 


#line 49 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/posix_types.h" 2


#line 16 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 2


typedef __u32 __kernel_dev_t;

typedef __kernel_fd_set		fd_set;
typedef __kernel_dev_t		dev_t;
typedef __kernel_ino_t		ino_t;
typedef __kernel_mode_t		mode_t;
typedef __kernel_nlink_t	nlink_t;
typedef __kernel_off_t		off_t;
typedef __kernel_pid_t		pid_t;
typedef __kernel_daddr_t	daddr_t;
typedef __kernel_key_t		key_t;
typedef __kernel_suseconds_t	suseconds_t;
typedef __kernel_timer_t	timer_t;
typedef __kernel_clockid_t	clockid_t;
typedef __kernel_mqd_t		mqd_t;

typedef _Bool			bool;

typedef __kernel_uid32_t	uid_t;
typedef __kernel_gid32_t	gid_t;
typedef __kernel_uid16_t        uid16_t;
typedef __kernel_gid16_t        gid16_t;

typedef unsigned long		uintptr_t;


/* This is defined by include/asm-{arch}/posix_types.h */
typedef __kernel_old_uid_t	old_uid_t;
typedef __kernel_old_gid_t	old_gid_t;


typedef __kernel_loff_t		loff_t;

/*
 * The following typedefs are also protected by individual ifdefs for
 * historical reasons:
 */


typedef __kernel_size_t		size_t;



typedef __kernel_ssize_t	ssize_t;



typedef __kernel_ptrdiff_t	ptrdiff_t;



typedef __kernel_time_t		time_t;



typedef __kernel_clock_t	clock_t;



typedef __kernel_caddr_t	caddr_t;

/* bsd */
typedef unsigned char		u_char;
typedef unsigned short		u_short;
typedef unsigned int		u_int;
typedef unsigned long		u_long;

/* sysv */
typedef unsigned char		unchar;
typedef unsigned short		ushort;
typedef unsigned int		uint;
typedef unsigned long		ulong;




typedef		__u8		u_int8_t;
typedef		__s8		int8_t;
typedef		__u16		u_int16_t;
typedef		__s16		int16_t;
typedef		__u32		u_int32_t;
typedef		__s32		int32_t;


typedef		__u8		uint8_t;
typedef		__u16		uint16_t;
typedef		__u32		uint32_t;


typedef		__u64		uint64_t;
typedef		__u64		u_int64_t;
typedef		__s64		int64_t;

/* this is a special 64bit data type that is 8-byte aligned */




/**
 * The type used for indexing onto a disc or disc partition.
 *
 * Linux always considers sectors to be 512 bytes long independently
 * of the devices real block size.
 *
 * blkcnt_t is the type of the inode's block count.
 */
#if definedEx(CONFIG_LBDAF)
typedef u64 sector_t;
typedef u64 blkcnt_t;
#endif
#if !(definedEx(CONFIG_LBDAF))
typedef unsigned long sector_t;
typedef unsigned long blkcnt_t;
#endif
/*
 * The type of an index into the pagecache.  Use a #define so asm/types.h
 * can override it.
 */




/*
 * Below are truly Linux-specific types that should never collide with
 * any application/library that wants linux/types.h.
 */



#if 1

#endif


#if 1

#endif
typedef __u16  __le16;
typedef __u16  __be16;
typedef __u32  __le32;
typedef __u32  __be32;
typedef __u64  __le64;
typedef __u64  __be64;

typedef __u16  __sum16;
typedef __u32  __wsum;


typedef unsigned  gfp_t;
typedef unsigned  fmode_t;

#if definedEx(CONFIG_PHYS_ADDR_T_64BIT)
typedef u64 phys_addr_t;
#endif
#if !(definedEx(CONFIG_PHYS_ADDR_T_64BIT))
typedef u32 phys_addr_t;
#endif
typedef phys_addr_t resource_size_t;

typedef struct {
	volatile int counter;
} atomic_t;

#if definedEx(CONFIG_64BIT)
typedef struct {
	volatile long counter;
} atomic64_t;
#endif
struct ustat {
	__kernel_daddr_t	f_tfree;
	__kernel_ino_t		f_tinode;
	char			f_fname[6];
	char			f_fpack[6];
};




#line 12 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/thread_info.h" 2

struct timespec;
struct compat_timespec;

/*
 * System call restart block.
 */
struct restart_block {
	long (*fn)(struct restart_block *);
	union {
		struct {
			unsigned long arg0, arg1, arg2, arg3;
		};
		/* For futex_wait and futex_wait_requeue_pi */
		struct {
			u32 *uaddr;
			u32 val;
			u32 flags;
			u32 bitset;
			u64 time;
			u32 *uaddr2;
		} futex;
		/* For nanosleep */
		struct {
			clockid_t index;
			struct timespec  *rmtp;
#if definedEx(CONFIG_COMPAT)
			struct compat_timespec  *compat_rmtp;
#endif
			u64 expires;
		} nanosleep;
		/* For poll */
		struct {
			struct pollfd  *ufds;
			int nfds;
			int has_timeout;
			unsigned long tv_sec;
			unsigned long tv_nsec;
		} poll;
	};
};

extern long do_no_restart_syscall(struct restart_block *parm);

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/bitops.h" 1


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/types.h" 1













 




#line 5 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/bitops.h" 2








/*
 * Include this here because some architectures need generic_ffs/fls in
 * scope
 */
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bitops.h" 1



/*
 * Copyright 1992, Linus Torvalds.
 *
 * Note: inlines with more than a single statement should be marked
 * __always_inline to avoid problems with older gcc's inlining heuristics.
 */




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 17 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bitops.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/alternative.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/alternative.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/stddef.h" 1








 



	
	





 



#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/alternative.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/stringify.h" 1



/* Indirect stringification.  Doing two levels allows the parameter to be a
 * macro itself.  For example, compile with -DFOO=bar, __stringify(FOO)
 * converts to "bar".
 */





#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/alternative.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/asm.h" 1






#if 1


#endif


 























/* Exception table entry */






#if 1





#endif

#line 9 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/alternative.h" 2

/*
 * Alternative inline assembly for SMP.
 *
 * The LOCK_PREFIX macro defined here replaces the LOCK and
 * LOCK_PREFIX macros used everywhere in the source tree.
 *
 * SMP alternatives use the same data structures as the other
 * alternatives and the X86_FEATURE_UP flag to indicate the case of a
 * UP system running a SMP kernel.  The existing apply_alternatives()
 * works fine for patching a SMP kernel for UP.
 *
 * The SMP alternative tables can be kept after boot and contain both
 * UP and SMP versions of the instructions to allow switching back to
 * SMP at runtime, when hotplugging in a new CPU, which is especially
 * useful in virtualized environments.
 *
 * The very common lock prefix is handled as special case in a
 * separate table which is a pure address list without replacement ptr
 * and size information.  That keeps the table sizes small.
 */

#if definedEx(CONFIG_SMP)







#endif
#if !(definedEx(CONFIG_SMP))

#endif
/* This must be included *after* the definition of LOCK_PREFIX */
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/cpufeature.h" 1
/*
 * Defines x86 CPU feature bits
 */



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/required-features.h" 1



/* Define minimum CPUID feature set for kernel These bits are checked
   really early to actually display a visible error message before the
   kernel dies.  Make sure to assign features to the proper mask!

   Some requirements that are not in CPUID yet are also in the
   CONFIG_X86_MINIMUM_CPU_FAMILY which is checked too.

   The real information is in arch/x86/Kconfig.cpu, this just converts
   the CONFIGs into a bitmask */

#if !(definedEx(CONFIG_MATH_EMULATION))

#endif
#if definedEx(CONFIG_MATH_EMULATION)

#endif
#if (definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64))

#endif
#if !((definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64)))

#endif


 



 

#if definedEx(CONFIG_X86_USE_3DNOW)

#endif
#if !(definedEx(CONFIG_X86_USE_3DNOW))

#endif
#if (definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64))

#endif
#if !((definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64)))

#endif
#if definedEx(CONFIG_X86_64)




#if 1


#endif





#endif
#if !(definedEx(CONFIG_X86_64))







#endif















#line 9 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/cpufeature.h" 2



/*
 * Note: If the comment begins with a quoted string, that string is used
 * in /proc/cpuinfo instead of the macro name.  If the string is "",
 * this feature bit is not displayed in /proc/cpuinfo at all.
 */

/* Intel-defined CPU features, CPUID level 0x00000001 (edx), word 0 */















					  /* (plus FCMOVcc, FCOMI with FPU) */
















/* AMD-defined CPU features, CPUID level 0x80000001, word 1 */
/* Don't duplicate feature flags which are redundant with Intel! */











/* Transmeta-defined CPU features, CPUID level 0x80860001, word 2 */




/* Other features, Linux-defined mapping, word 3 */
/* This range is used for feature bits which conflict or are synthesized */




/* cpu types for specific tunings: */


























/* Intel-defined CPU features, CPUID level 0x00000001 (ecx), word 4 */



























/* VIA/Cyrix/Centaur-defined CPU features, CPUID level 0xC0000001, word 5 */











/* More extended AMD flags: CPUID level 0x80000001, ecx, word 6 */
















/*
 * Auxiliary flags: Linux defined - For features scattered in various
 * CPUID levels like 0x6, 0xA etc
 */



/* Virtualization flags: Linux defined */







#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/bitops.h" 1

























	
	
	
	




	
	
	
	
		
	




	









	









	









	









	









	









	




	
		
	













	
		
 

	












				    









					 










				   









				   









					
					




#line 176 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/cpufeature.h" 2

extern const char * const x86_cap_flags[9*32];
extern const char * const x86_power_flags[32];














































































 

#if definedEx(CONFIG_X86_64)


















#endif


#line 45 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/alternative.h" 2

struct alt_instr {
	u8 *instr;		/* original instruction */
	u8 *replacement;
	u8  cpuid;		/* cpuid bit set for replacement */
	u8  instrlen;		/* length of original instruction */
	u8  replacementlen;	/* length of new instruction, <= instrlen */
	u8  pad1;
#if definedEx(CONFIG_X86_64)
	u32 pad2;
#endif
};

extern void alternative_instructions(void);
extern void apply_alternatives(struct alt_instr *start, struct alt_instr *end);

struct module;

#if definedEx(CONFIG_SMP)
extern void alternatives_smp_module_add(struct module *mod, char *name,
					void *locks, void *locks_end,
					void *text, void *text_end);
extern void alternatives_smp_module_del(struct module *mod);
extern void alternatives_smp_switch(int smp);
#endif
#if !(definedEx(CONFIG_SMP))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void alternatives_smp_module_add(struct module *mod, char *name,
					       void *locks, void *locks_end,
					       void *text, void *text_end) {}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void alternatives_smp_module_del(struct module *mod) {}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void alternatives_smp_switch(int smp) {}
#endif
/* alternative assembly primitive: */
















/*
 * Alternative instructions for different CPU types or capabilities.
 *
 * This allows to use optimized instructions even on generic binary
 * kernels.
 *
 * length of oldinstr must be longer or equal the length of newinstr
 * It can be padded with nops as needed.
 *
 * For non barrier like inlines please define new variants
 * without volatile and memory clobber.
 */



/*
 * Alternative inline assembly with input.
 *
 * Pecularities:
 * No memory clobber here.
 * Argument numbers start with 1.
 * Best is to use constraints that are fixed size (like (%1) ... "r")
 * If you use variable sized constraints like "m" or "g" in the
 * replacement make sure to pad to the worst case length.
 * Leaving an unused argument 0 to keep API compatibility.
 */




/* Like alternative_input, but with a single output argument */




/*
 * use this macro(s) if you need more than one output parameter
 * in alternative_io
 */


struct paravirt_patch_site;


		    
#if 1
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void apply_paravirt(struct paravirt_patch_site *start,
				  struct paravirt_patch_site *end)
{}


#endif
/*
 * Clear and restore the kernel write-protection flag on the local CPU.
 * Allows the kernel to edit read-only pages.
 * Side-effect: any interrupt handler running between save and restore will have
 * the ability to write to read-only pages.
 *
 * Warning:
 * Code patching in the UP case is safe if NMIs and MCE handlers are stopped and
 * no thread can be preempted in the instructions being modified (no iret to an
 * invalid instruction possible) or if the instructions are changed from a
 * consistent state to another consistent state atomically.
 * More care must be taken when modifying code in the SMP case because of
 * Intel's errata.
 * On the local CPU you need to be protected again NMI or MCE handlers seeing an
 * inconsistent instruction while you patch.
 */
extern void *text_poke(void *addr, const void *opcode, size_t len);


#line 18 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bitops.h" 2

/*
 * These have to be done with inline assembly: that way the bit-setting
 * is guaranteed to be atomic. All bit operations return 0 if the bit
 * was cleared before the operation and != 0 if it was not.
 *
 * bit 0 is the LSB of addr; bit 32 is the LSB of (addr+1).
 */





#if 1

#endif


/*
 * We do the locked ops that don't return the old value as
 * a mask operation on a byte.
 */




/**
 * set_bit - Atomically set a bit in memory
 * @nr: the bit to set
 * @addr: the address to start counting from
 *
 * This function is atomic and may not be reordered.  See __set_bit()
 * if you do not require the atomic guarantees.
 *
 * Note: there are no guarantees that this function will not be reordered
 * on non x86 architectures, so if you are writing portable code,
 * make sure not to rely on its reordering guarantees.
 *
 * Note that @nr may be almost arbitrarily large; this function is not
 * restricted to acting on a single-word quantity.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline)) void
set_bit(unsigned int nr, volatile unsigned long *addr)
{
	if ((__builtin_constant_p(nr))) {
		asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "orb %1,%0"
			: "+m" (*(volatile long *) ((void *)(addr) +((nr)>>3)))
			: "iq" ((u8)(1 << ((nr) & 7)))
			: "memory");
	} else {
		asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "bts %1,%0"
			: "+m" (*(volatile long *) (addr)) : "Ir" (nr) : "memory");
	}
}

/**
 * __set_bit - Set a bit in memory
 * @nr: the bit to set
 * @addr: the address to start counting from
 *
 * Unlike set_bit(), this function is non-atomic and may be reordered.
 * If it's called on the same region of memory simultaneously, the effect
 * may be that only one operation succeeds.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __set_bit(int nr, volatile unsigned long *addr)
{
	asm volatile("bts %1,%0" : "+m" (*(volatile long *) (addr)) : "Ir" (nr) : "memory");
}

/**
 * clear_bit - Clears a bit in memory
 * @nr: Bit to clear
 * @addr: Address to start counting from
 *
 * clear_bit() is atomic and may not be reordered.  However, it does
 * not contain a memory barrier, so if it is used for locking purposes,
 * you should call smp_mb__before_clear_bit() and/or smp_mb__after_clear_bit()
 * in order to ensure changes are visible on other processors.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline)) void
clear_bit(int nr, volatile unsigned long *addr)
{
	if ((__builtin_constant_p(nr))) {
		asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "andb %1,%0"
			: "+m" (*(volatile long *) ((void *)(addr) +((nr)>>3)))
			: "iq" ((u8)~(1 << ((nr) & 7))));
	} else {
		asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "btr %1,%0"
			: "+m" (*(volatile long *) (addr))
			: "Ir" (nr));
	}
}

/*
 * clear_bit_unlock - Clears a bit in memory
 * @nr: Bit to clear
 * @addr: Address to start counting from
 *
 * clear_bit() is atomic and implies release semantics before the memory
 * operation. It can be used for an unlock.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void clear_bit_unlock(unsigned nr, volatile unsigned long *addr)
{
	__asm__ __volatile__("": : :"memory");
	clear_bit(nr, addr);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __clear_bit(int nr, volatile unsigned long *addr)
{
	asm volatile("btr %1,%0" : "+m" (*(volatile long *) (addr)) : "Ir" (nr));
}

/*
 * __clear_bit_unlock - Clears a bit in memory
 * @nr: Bit to clear
 * @addr: Address to start counting from
 *
 * __clear_bit() is non-atomic and implies release semantics before the memory
 * operation. It can be used for an unlock if no other CPUs can concurrently
 * modify other bits in the word.
 *
 * No memory barrier is required here, because x86 cannot reorder stores past
 * older loads. Same principle as spin_unlock.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __clear_bit_unlock(unsigned nr, volatile unsigned long *addr)
{
	__asm__ __volatile__("": : :"memory");
	__clear_bit(nr, addr);
}




/**
 * __change_bit - Toggle a bit in memory
 * @nr: the bit to change
 * @addr: the address to start counting from
 *
 * Unlike change_bit(), this function is non-atomic and may be reordered.
 * If it's called on the same region of memory simultaneously, the effect
 * may be that only one operation succeeds.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __change_bit(int nr, volatile unsigned long *addr)
{
	asm volatile("btc %1,%0" : "+m" (*(volatile long *) (addr)) : "Ir" (nr));
}

/**
 * change_bit - Toggle a bit in memory
 * @nr: Bit to change
 * @addr: Address to start counting from
 *
 * change_bit() is atomic and may not be reordered.
 * Note that @nr may be almost arbitrarily large; this function is not
 * restricted to acting on a single-word quantity.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void change_bit(int nr, volatile unsigned long *addr)
{
	if ((__builtin_constant_p(nr))) {
		asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "xorb %1,%0"
			: "+m" (*(volatile long *) ((void *)(addr) +((nr)>>3)))
			: "iq" ((u8)(1 << ((nr) & 7))));
	} else {
		asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "btc %1,%0"
			: "+m" (*(volatile long *) (addr))
			: "Ir" (nr));
	}
}

/**
 * test_and_set_bit - Set a bit and return its old value
 * @nr: Bit to set
 * @addr: Address to count from
 *
 * This operation is atomic and cannot be reordered.
 * It also implies a memory barrier.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int test_and_set_bit(int nr, volatile unsigned long *addr)
{
	int oldbit;

	asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "bts %2,%1\n\t"
		     "sbb %0,%0" : "=r" (oldbit), "+m" (*(volatile long *) (addr)) : "Ir" (nr) : "memory");

	return oldbit;
}

/**
 * test_and_set_bit_lock - Set a bit and return its old value for lock
 * @nr: Bit to set
 * @addr: Address to count from
 *
 * This is the same as test_and_set_bit on x86.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline)) int
test_and_set_bit_lock(int nr, volatile unsigned long *addr)
{
	return test_and_set_bit(nr, addr);
}

/**
 * __test_and_set_bit - Set a bit and return its old value
 * @nr: Bit to set
 * @addr: Address to count from
 *
 * This operation is non-atomic and can be reordered.
 * If two examples of this operation race, one can appear to succeed
 * but actually fail.  You must protect multiple accesses with a lock.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int __test_and_set_bit(int nr, volatile unsigned long *addr)
{
	int oldbit;

	asm("bts %2,%1\n\t"
	    "sbb %0,%0"
	    : "=r" (oldbit), "+m" (*(volatile long *) (addr))
	    : "Ir" (nr));
	return oldbit;
}

/**
 * test_and_clear_bit - Clear a bit and return its old value
 * @nr: Bit to clear
 * @addr: Address to count from
 *
 * This operation is atomic and cannot be reordered.
 * It also implies a memory barrier.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int test_and_clear_bit(int nr, volatile unsigned long *addr)
{
	int oldbit;

	asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "btr %2,%1\n\t"
		     "sbb %0,%0"
		     : "=r" (oldbit), "+m" (*(volatile long *) (addr)) : "Ir" (nr) : "memory");

	return oldbit;
}

/**
 * __test_and_clear_bit - Clear a bit and return its old value
 * @nr: Bit to clear
 * @addr: Address to count from
 *
 * This operation is non-atomic and can be reordered.
 * If two examples of this operation race, one can appear to succeed
 * but actually fail.  You must protect multiple accesses with a lock.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int __test_and_clear_bit(int nr, volatile unsigned long *addr)
{
	int oldbit;

	asm volatile("btr %2,%1\n\t"
		     "sbb %0,%0"
		     : "=r" (oldbit), "+m" (*(volatile long *) (addr))
		     : "Ir" (nr));
	return oldbit;
}

/* WARNING: non atomic and it can be reordered! */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int __test_and_change_bit(int nr, volatile unsigned long *addr)
{
	int oldbit;

	asm volatile("btc %2,%1\n\t"
		     "sbb %0,%0"
		     : "=r" (oldbit), "+m" (*(volatile long *) (addr))
		     : "Ir" (nr) : "memory");

	return oldbit;
}

/**
 * test_and_change_bit - Change a bit and return its old value
 * @nr: Bit to change
 * @addr: Address to count from
 *
 * This operation is atomic and cannot be reordered.
 * It also implies a memory barrier.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int test_and_change_bit(int nr, volatile unsigned long *addr)
{
	int oldbit;

	asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "btc %2,%1\n\t"
		     "sbb %0,%0"
		     : "=r" (oldbit), "+m" (*(volatile long *) (addr)) : "Ir" (nr) : "memory");

	return oldbit;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline)) int constant_test_bit(unsigned int nr, const volatile unsigned long *addr)
{
	return ((1UL << (nr % 
#if (definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT))
64
#endif
#if (!(definedEx(CONFIG_64BIT)) && !((definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT))))
32
#endif
)) &
		(((unsigned long *)addr)[nr / 
#if (definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT))
64
#endif
#if (!(definedEx(CONFIG_64BIT)) && !((definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT))))
32
#endif
])) != 0;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int variable_test_bit(int nr, volatile const unsigned long *addr)
{
	int oldbit;

	asm volatile("bt %2,%1\n\t"
		     "sbb %0,%0"
		     : "=r" (oldbit)
		     : "m" (*(unsigned long *)addr), "Ir" (nr));

	return oldbit;
}














/**
 * __ffs - find first set bit in word
 * @word: The word to search
 *
 * Undefined if no bit exists, so code should check against 0 first.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long __ffs(unsigned long word)
{
	asm("bsf %1,%0"
		: "=r" (word)
		: "rm" (word));
	return word;
}

/**
 * ffz - find first zero bit in word
 * @word: The word to search
 *
 * Undefined if no zero exists, so code should check against ~0UL first.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long ffz(unsigned long word)
{
	asm("bsf %1,%0"
		: "=r" (word)
		: "r" (~word));
	return word;
}

/*
 * __fls: find last set bit in word
 * @word: The word to search
 *
 * Undefined if no set bit exists, so code should check against 0 first.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long __fls(unsigned long word)
{
	asm("bsr %1,%0"
	    : "=r" (word)
	    : "rm" (word));
	return word;
}


/**
 * ffs - find first set bit in word
 * @x: the word to search
 *
 * This is defined the same way as the libc and compiler builtin ffs
 * routines, therefore differs in spirit from the other bitops.
 *
 * ffs(value) returns 0 if value is 0 or the position of the first
 * set bit if value is nonzero. The first (least significant) bit
 * is at position 1.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int ffs(int x)
{
	int r;

	asm("bsfl %1,%0\n\t"
	    "cmovzl %2,%0"
	    : "=r" (r) : "rm" (x), "r" (-1));
 	
	    
	    
	    

	return r + 1;
}

/**
 * fls - find last set bit in word
 * @x: the word to search
 *
 * This is defined in a similar way as the libc and compiler builtin
 * ffs, but returns the position of the most significant set bit.
 *
 * fls(value) returns 0 if value is 0 or the position of the last
 * set bit if value is nonzero. The last (most significant) bit is
 * at position 32.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int fls(int x)
{
	int r;

	asm("bsrl %1,%0\n\t"
	    "cmovzl %2,%0"
	    : "=&r" (r) : "rm" (x), "rm" (-1));
 	
	    
	    
	    

	return r + 1;
}




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitops/sched.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitops/sched.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/types.h" 1













 




#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitops/sched.h" 2

/*
 * Every architecture must define this function. It's the fastest
 * way of searching a 100-bit bitmap.  It's guaranteed that at least
 * one of the 100 bits is cleared.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int sched_find_first_bit(const unsigned long *b)
{
#if (definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT))
	if (b[0])
		return __ffs(b[0]);
	return __ffs(b[1]) + 64;
#endif
#if (!((definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT))) && !((definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT))))
	if (b[0])
		return __ffs(b[0]);
	if (b[1])
		return __ffs(b[1]) + 32;
	if (b[2])
		return __ffs(b[2]) + 64;
	return __ffs(b[3]) + 96;
#endif


}


#line 445 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bitops.h" 2



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitops/hweight.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/types.h" 1













 




#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitops/hweight.h" 2

extern unsigned int hweight32(unsigned int w);
extern unsigned int hweight16(unsigned int w);
extern unsigned int hweight8(unsigned int w);
extern unsigned long hweight64(__u64 w);


#line 449 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bitops.h" 2


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitops/fls64.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/types.h" 1













 




#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitops/fls64.h" 2

/**
 * fls64 - find last set bit in a 64-bit word
 * @x: the word to search
 *
 * This is defined in a similar way as the libc and compiler builtin
 * ffsll, but returns the position of the most significant set bit.
 *
 * fls64(value) returns 0 if value is 0 or the position of the last
 * set bit if value is nonzero. The last (most significant) bit is
 * at position 64.
 */
#if !((definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT)))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline)) int fls64(__u64 x)
{
	__u32 h = x >> 32;
	if (h)
		return fls(h) + 32;
	return fls(x);
}
#endif
#if (definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline)) int fls64(__u64 x)
{
	if (x == 0)
		return 0;
	return __fls(x) + 1;
}
#endif



#line 453 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bitops.h" 2


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitops/ext2-non-atomic.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitops/le.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/types.h" 1













 




#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitops/le.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/byteorder.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/byteorder/little_endian.h" 1









#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 13 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/byteorder/little_endian.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/swab.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/swab.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/swab.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/swab.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/swab.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/swab.h" 2

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((__const__)) __u32 __arch_swab32(__u32 val)
{


	
 	
	    
	    
	    
	    

#if 1
	asm("bswapl %0"
	    : "=r" (val)
	    : "0" (val));
#endif
	return val;
}


static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((__const__)) __u64 __arch_swab64(__u64 val)
{

	
		
			
			
		
		
	
	

	
	    
	    
 	
	
	
	    
	    

	
#if 1
	asm("bswapq %0"
	    : "=r" (val)
	    : "0" (val));
	return val;
#endif
}



#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/swab.h" 2

/*
 * casts are necessary for constants, because we never know how for sure
 * how U/UL/ULL map to __u16, __u32, __u64. At least not in a portable way.
 */




























/*
 * Implement the following as inlines, but define the interface using
 * macros to allow constant folding when possible:
 * ___swab16, ___swab32, ___swab64, ___swahw32, ___swahb32
 */

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((__const__)) __u16 __fswab16(__u16 val)
{

	
#if 1
	return ((__u16)( (((__u16)(val) & (__u16)0x00ffU) << 8) | (((__u16)(val) & (__u16)0xff00U) >> 8)));
#endif
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((__const__)) __u32 __fswab32(__u32 val)
{

	return __arch_swab32(val);
 	

}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((__const__)) __u64 __fswab64(__u64 val)
{

	return __arch_swab64(val);
 	
	
	
 	

}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((__const__)) __u32 __fswahw32(__u32 val)
{

	
#if 1
	return ((__u32)( (((__u32)(val) & (__u32)0x0000ffffUL) << 16) | (((__u32)(val) & (__u32)0xffff0000UL) >> 16)));
#endif
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((__const__)) __u32 __fswahb32(__u32 val)
{

	
#if 1
	return ((__u32)( (((__u32)(val) & (__u32)0x00ff00ffUL) << 8) | (((__u32)(val) & (__u32)0xff00ff00UL) >> 8)));
#endif
}

/**
 * __swab16 - return a byteswapped 16-bit value
 * @x: value to byteswap
 */





/**
 * __swab32 - return a byteswapped 32-bit value
 * @x: value to byteswap
 */





/**
 * __swab64 - return a byteswapped 64-bit value
 * @x: value to byteswap
 */





/**
 * __swahw32 - return a word-swapped 32-bit value
 * @x: value to wordswap
 *
 * __swahw32(0x12340000) is 0x00001234
 */





/**
 * __swahb32 - return a high and low byte-swapped 32-bit value
 * @x: value to byteswap
 *
 * __swahb32(0x12345678) is 0x34127856
 */





/**
 * __swab16p - return a byteswapped 16-bit value from a pointer
 * @p: pointer to a naturally-aligned 16-bit value
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u16 __swab16p(const __u16 *p)
{

	
#if 1
	return (__builtin_constant_p((__u16)(*p)) ? ((__u16)( (((__u16)(*p) & (__u16)0x00ffU) << 8) | (((__u16)(*p) & (__u16)0xff00U) >> 8))) : __fswab16(*p));
#endif
}

/**
 * __swab32p - return a byteswapped 32-bit value from a pointer
 * @p: pointer to a naturally-aligned 32-bit value
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u32 __swab32p(const __u32 *p)
{

	
#if 1
	return (__builtin_constant_p((__u32)(*p)) ? ((__u32)( (((__u32)(*p) & (__u32)0x000000ffUL) << 24) | (((__u32)(*p) & (__u32)0x0000ff00UL) << 8) | (((__u32)(*p) & (__u32)0x00ff0000UL) >> 8) | (((__u32)(*p) & (__u32)0xff000000UL) >> 24))) : __fswab32(*p));
#endif
}

/**
 * __swab64p - return a byteswapped 64-bit value from a pointer
 * @p: pointer to a naturally-aligned 64-bit value
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u64 __swab64p(const __u64 *p)
{

	
#if 1
	return (__builtin_constant_p((__u64)(*p)) ? ((__u64)( (((__u64)(*p) & (__u64)0x00000000000000ffULL) << 56) | (((__u64)(*p) & (__u64)0x000000000000ff00ULL) << 40) | (((__u64)(*p) & (__u64)0x0000000000ff0000ULL) << 24) | (((__u64)(*p) & (__u64)0x00000000ff000000ULL) << 8) | (((__u64)(*p) & (__u64)0x000000ff00000000ULL) >> 8) | (((__u64)(*p) & (__u64)0x0000ff0000000000ULL) >> 24) | (((__u64)(*p) & (__u64)0x00ff000000000000ULL) >> 40) | (((__u64)(*p) & (__u64)0xff00000000000000ULL) >> 56))) : __fswab64(*p));
#endif
}

/**
 * __swahw32p - return a wordswapped 32-bit value from a pointer
 * @p: pointer to a naturally-aligned 32-bit value
 *
 * See __swahw32() for details of wordswapping.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u32 __swahw32p(const __u32 *p)
{

	
#if 1
	return (__builtin_constant_p((__u32)(*p)) ? ((__u32)( (((__u32)(*p) & (__u32)0x0000ffffUL) << 16) | (((__u32)(*p) & (__u32)0xffff0000UL) >> 16))) : __fswahw32(*p));
#endif
}

/**
 * __swahb32p - return a high and low byteswapped 32-bit value from a pointer
 * @p: pointer to a naturally-aligned 32-bit value
 *
 * See __swahb32() for details of high/low byteswapping.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u32 __swahb32p(const __u32 *p)
{

	
#if 1
	return (__builtin_constant_p((__u32)(*p)) ? ((__u32)( (((__u32)(*p) & (__u32)0x00ff00ffUL) << 8) | (((__u32)(*p) & (__u32)0xff00ff00UL) >> 8))) : __fswahb32(*p));
#endif
}

/**
 * __swab16s - byteswap a 16-bit value in-place
 * @p: pointer to a naturally-aligned 16-bit value
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __swab16s(__u16 *p)
{

	
#if 1
	*p = __swab16p(p);
#endif
}
/**
 * __swab32s - byteswap a 32-bit value in-place
 * @p: pointer to a naturally-aligned 32-bit value
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __swab32s(__u32 *p)
{

	
#if 1
	*p = __swab32p(p);
#endif
}

/**
 * __swab64s - byteswap a 64-bit value in-place
 * @p: pointer to a naturally-aligned 64-bit value
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __swab64s(__u64 *p)
{

	
#if 1
	*p = __swab64p(p);
#endif
}

/**
 * __swahw32s - wordswap a 32-bit value in-place
 * @p: pointer to a naturally-aligned 32-bit value
 *
 * See __swahw32() for details of wordswapping
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __swahw32s(__u32 *p)
{

	
#if 1
	*p = __swahw32p(p);
#endif
}

/**
 * __swahb32s - high and low byteswap a 32-bit value in-place
 * @p: pointer to a naturally-aligned 32-bit value
 *
 * See __swahb32() for details of high and low byte swapping
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __swahb32s(__u32 *p)
{

	
#if 1
	*p = __swahb32p(p);
#endif
}



















#line 14 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/byteorder/little_endian.h" 2






























static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __le64 __cpu_to_le64p(const __u64 *p)
{
	return ( __le64)*p;
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u64 __le64_to_cpup(const __le64 *p)
{
	return ( __u64)*p;
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __le32 __cpu_to_le32p(const __u32 *p)
{
	return ( __le32)*p;
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u32 __le32_to_cpup(const __le32 *p)
{
	return ( __u32)*p;
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __le16 __cpu_to_le16p(const __u16 *p)
{
	return ( __le16)*p;
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u16 __le16_to_cpup(const __le16 *p)
{
	return ( __u16)*p;
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __be64 __cpu_to_be64p(const __u64 *p)
{
	return ( __be64)__swab64p(p);
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u64 __be64_to_cpup(const __be64 *p)
{
	return __swab64p((__u64 *)p);
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __be32 __cpu_to_be32p(const __u32 *p)
{
	return ( __be32)__swab32p(p);
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u32 __be32_to_cpup(const __be32 *p)
{
	return __swab32p((__u32 *)p);
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __be16 __cpu_to_be16p(const __u16 *p)
{
	return ( __be16)__swab16p(p);
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u16 __be16_to_cpup(const __be16 *p)
{
	return __swab16p((__u16 *)p);
}














#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/byteorder/generic.h" 1



/*
 * linux/byteorder_generic.h
 * Generic Byte-reordering support
 *
 * The "... p" macros, like le64_to_cpup, can be used with pointers
 * to unaligned data, but there will be a performance penalty on 
 * some architectures.  Use get_unaligned for unaligned data.
 *
 * Francois-Rene Rideau <fare@tunes.org> 19970707
 *    gathered all the good ideas from all asm-foo/byteorder.h into one file,
 *    cleaned them up.
 *    I hope it is compliant with non-GCC compilers.
 *    I decided to put __BYTEORDER_HAS_U64__ in byteorder.h,
 *    because I wasn't sure it would be ok to put it in types.h
 *    Upgraded it to 2.1.43
 * Francois-Rene Rideau <fare@tunes.org> 19971012
 *    Upgraded it to 2.1.57
 *    to please Linus T., replaced huge #ifdef's between little/big endian
 *    by nestedly #include'd files.
 * Francois-Rene Rideau <fare@tunes.org> 19971205
 *    Made it to 2.1.71; now a facelift:
 *    Put files under include/linux/byteorder/
 *    Split swab from generic support.
 *
 * TODO:
 *   = Regular kernel maintainers could also replace all these manual
 *    byteswap macros that remain, disseminated among drivers,
 *    after some grep or the sources...
 *   = Linus might want to rename all these macros and files to fit his taste,
 *    to fit his personal naming scheme.
 *   = it seems that a few drivers would also appreciate
 *    nybble swapping support...
 *   = every architecture could add their byteswap macro in asm/byteorder.h
 *    see how some architectures already do (i386, alpha, ppc, etc)
 *   = cpu_to_beXX and beXX_to_cpu might some day need to be well
 *    distinguished throughout the kernel. This is not the case currently,
 *    since little endian, big endian, and pdp endian machines needn't it.
 *    But this might be the case for, say, a port of Linux to 20/21 bit
 *    architectures (and F21 Linux addict around?).
 */

/*
 * The following macros are to be defined by <asm/byteorder.h>:
 *
 * Conversion of long and short int between network and host format
 *	ntohl(__u32 x)
 *	ntohs(__u16 x)
 *	htonl(__u32 x)
 *	htons(__u16 x)
 * It seems that some programs (which? where? or perhaps a standard? POSIX?)
 * might like the above to be functions, not macros (why?).
 * if that's true, then detect them, and take measures.
 * Anyway, the measure is: define only ___ntohl as a macro instead,
 * and in a separate file, have
 * unsigned long inline ntohl(x){return ___ntohl(x);}
 *
 * The same for constant arguments
 *	__constant_ntohl(__u32 x)
 *	__constant_ntohs(__u16 x)
 *	__constant_htonl(__u32 x)
 *	__constant_htons(__u16 x)
 *
 * Conversion of XX-bit integers (16- 32- or 64-)
 * between native CPU format and little/big endian format
 * 64-bit stuff only defined for proper architectures
 *	cpu_to_[bl]eXX(__uXX x)
 *	[bl]eXX_to_cpu(__uXX x)
 *
 * The same, but takes a pointer to the value to convert
 *	cpu_to_[bl]eXXp(__uXX x)
 *	[bl]eXX_to_cpup(__uXX x)
 *
 * The same, but change in situ
 *	cpu_to_[bl]eXXs(__uXX x)
 *	[bl]eXX_to_cpus(__uXX x)
 *
 * See asm-foo/byteorder.h for examples of how to provide
 * architecture-optimized versions
 *
 */






































/*
 * They have to be macros in order to do the constant folding
 * correctly - if the argument passed into a inline function
 * it is no longer constant according to gcc..
 */
















static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void le16_add_cpu(__le16 *var, u16 val)
{
	*var = (( __le16)(__u16)((( __u16)(__le16)(*var)) + val));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void le32_add_cpu(__le32 *var, u32 val)
{
	*var = (( __le32)(__u32)((( __u32)(__le32)(*var)) + val));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void le64_add_cpu(__le64 *var, u64 val)
{
	*var = (( __le64)(__u64)((( __u64)(__le64)(*var)) + val));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void be16_add_cpu(__be16 *var, u16 val)
{
	*var = (( __be16)(__builtin_constant_p((__u16)(((__builtin_constant_p((__u16)(( __u16)(__be16)(*var))) ?((__u16)((((__u16)(( __u16)(__be16)(*var)) &(__u16)0x00ffU) << 8) |(((__u16)(( __u16)(__be16)(*var)) &(__u16)0xff00U) >> 8))) : __fswab16(( __u16)(__be16)(*var))) + val))) ? ((__u16)( (((__u16)(((__builtin_constant_p((__u16)(( __u16)(__be16)(*var))) ?((__u16)((((__u16)(( __u16)(__be16)(*var)) &(__u16)0x00ffU) << 8) |(((__u16)(( __u16)(__be16)(*var)) &(__u16)0xff00U) >> 8))) : __fswab16(( __u16)(__be16)(*var))) + val)) & (__u16)0x00ffU) << 8) | (((__u16)(((__builtin_constant_p((__u16)(( __u16)(__be16)(*var))) ?((__u16)((((__u16)(( __u16)(__be16)(*var)) &(__u16)0x00ffU) << 8) |(((__u16)(( __u16)(__be16)(*var)) &(__u16)0xff00U) >> 8))) : __fswab16(( __u16)(__be16)(*var))) + val)) & (__u16)0xff00U) >> 8))) : __fswab16(((__builtin_constant_p((__u16)(( __u16)(__be16)(*var))) ?((__u16)((((__u16)(( __u16)(__be16)(*var)) &(__u16)0x00ffU) << 8) |(((__u16)(( __u16)(__be16)(*var)) &(__u16)0xff00U) >> 8))) : __fswab16(( __u16)(__be16)(*var))) + val))));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void be32_add_cpu(__be32 *var, u32 val)
{
	*var = (( __be32)(__builtin_constant_p((__u32)(((__builtin_constant_p((__u32)(( __u32)(__be32)(*var))) ?((__u32)((((__u32)(( __u32)(__be32)(*var)) &(__u32)0x000000ffUL) << 24) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0x0000ff00UL) << 8) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0x00ff0000UL) >> 8) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0xff000000UL) >> 24))) : __fswab32(( __u32)(__be32)(*var))) + val))) ? ((__u32)( (((__u32)(((__builtin_constant_p((__u32)(( __u32)(__be32)(*var))) ?((__u32)((((__u32)(( __u32)(__be32)(*var)) &(__u32)0x000000ffUL) << 24) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0x0000ff00UL) << 8) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0x00ff0000UL) >> 8) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0xff000000UL) >> 24))) : __fswab32(( __u32)(__be32)(*var))) + val)) & (__u32)0x000000ffUL) << 24) | (((__u32)(((__builtin_constant_p((__u32)(( __u32)(__be32)(*var))) ?((__u32)((((__u32)(( __u32)(__be32)(*var)) &(__u32)0x000000ffUL) << 24) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0x0000ff00UL) << 8) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0x00ff0000UL) >> 8) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0xff000000UL) >> 24))) : __fswab32(( __u32)(__be32)(*var))) + val)) & (__u32)0x0000ff00UL) << 8) | (((__u32)(((__builtin_constant_p((__u32)(( __u32)(__be32)(*var))) ?((__u32)((((__u32)(( __u32)(__be32)(*var)) &(__u32)0x000000ffUL) << 24) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0x0000ff00UL) << 8) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0x00ff0000UL) >> 8) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0xff000000UL) >> 24))) : __fswab32(( __u32)(__be32)(*var))) + val)) & (__u32)0x00ff0000UL) >> 8) | (((__u32)(((__builtin_constant_p((__u32)(( __u32)(__be32)(*var))) ?((__u32)((((__u32)(( __u32)(__be32)(*var)) &(__u32)0x000000ffUL) << 24) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0x0000ff00UL) << 8) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0x00ff0000UL) >> 8) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0xff000000UL) >> 24))) : __fswab32(( __u32)(__be32)(*var))) + val)) & (__u32)0xff000000UL) >> 24))) : __fswab32(((__builtin_constant_p((__u32)(( __u32)(__be32)(*var))) ?((__u32)((((__u32)(( __u32)(__be32)(*var)) &(__u32)0x000000ffUL) << 24) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0x0000ff00UL) << 8) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0x00ff0000UL) >> 8) |(((__u32)(( __u32)(__be32)(*var)) &(__u32)0xff000000UL) >> 24))) : __fswab32(( __u32)(__be32)(*var))) + val))));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void be64_add_cpu(__be64 *var, u64 val)
{
	*var = (( __be64)(__builtin_constant_p((__u64)(((__builtin_constant_p((__u64)(( __u64)(__be64)(*var))) ?((__u64)((((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000000000ffULL) << 56) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000000000ff00ULL) << 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000000000ff0000ULL) << 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000ff000000ULL) << 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000ff00000000ULL) >> 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000ff0000000000ULL) >> 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00ff000000000000ULL) >> 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0xff00000000000000ULL) >> 56))) : __fswab64(( __u64)(__be64)(*var))) + val))) ? ((__u64)( (((__u64)(((__builtin_constant_p((__u64)(( __u64)(__be64)(*var))) ?((__u64)((((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000000000ffULL) << 56) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000000000ff00ULL) << 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000000000ff0000ULL) << 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000ff000000ULL) << 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000ff00000000ULL) >> 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000ff0000000000ULL) >> 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00ff000000000000ULL) >> 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0xff00000000000000ULL) >> 56))) : __fswab64(( __u64)(__be64)(*var))) + val)) & (__u64)0x00000000000000ffULL) << 56) | (((__u64)(((__builtin_constant_p((__u64)(( __u64)(__be64)(*var))) ?((__u64)((((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000000000ffULL) << 56) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000000000ff00ULL) << 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000000000ff0000ULL) << 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000ff000000ULL) << 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000ff00000000ULL) >> 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000ff0000000000ULL) >> 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00ff000000000000ULL) >> 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0xff00000000000000ULL) >> 56))) : __fswab64(( __u64)(__be64)(*var))) + val)) & (__u64)0x000000000000ff00ULL) << 40) | (((__u64)(((__builtin_constant_p((__u64)(( __u64)(__be64)(*var))) ?((__u64)((((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000000000ffULL) << 56) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000000000ff00ULL) << 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000000000ff0000ULL) << 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000ff000000ULL) << 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000ff00000000ULL) >> 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000ff0000000000ULL) >> 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00ff000000000000ULL) >> 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0xff00000000000000ULL) >> 56))) : __fswab64(( __u64)(__be64)(*var))) + val)) & (__u64)0x0000000000ff0000ULL) << 24) | (((__u64)(((__builtin_constant_p((__u64)(( __u64)(__be64)(*var))) ?((__u64)((((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000000000ffULL) << 56) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000000000ff00ULL) << 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000000000ff0000ULL) << 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000ff000000ULL) << 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000ff00000000ULL) >> 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000ff0000000000ULL) >> 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00ff000000000000ULL) >> 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0xff00000000000000ULL) >> 56))) : __fswab64(( __u64)(__be64)(*var))) + val)) & (__u64)0x00000000ff000000ULL) << 8) | (((__u64)(((__builtin_constant_p((__u64)(( __u64)(__be64)(*var))) ?((__u64)((((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000000000ffULL) << 56) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000000000ff00ULL) << 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000000000ff0000ULL) << 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000ff000000ULL) << 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000ff00000000ULL) >> 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000ff0000000000ULL) >> 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00ff000000000000ULL) >> 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0xff00000000000000ULL) >> 56))) : __fswab64(( __u64)(__be64)(*var))) + val)) & (__u64)0x000000ff00000000ULL) >> 8) | (((__u64)(((__builtin_constant_p((__u64)(( __u64)(__be64)(*var))) ?((__u64)((((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000000000ffULL) << 56) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000000000ff00ULL) << 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000000000ff0000ULL) << 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000ff000000ULL) << 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000ff00000000ULL) >> 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000ff0000000000ULL) >> 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00ff000000000000ULL) >> 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0xff00000000000000ULL) >> 56))) : __fswab64(( __u64)(__be64)(*var))) + val)) & (__u64)0x0000ff0000000000ULL) >> 24) | (((__u64)(((__builtin_constant_p((__u64)(( __u64)(__be64)(*var))) ?((__u64)((((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000000000ffULL) << 56) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000000000ff00ULL) << 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000000000ff0000ULL) << 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000ff000000ULL) << 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000ff00000000ULL) >> 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000ff0000000000ULL) >> 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00ff000000000000ULL) >> 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0xff00000000000000ULL) >> 56))) : __fswab64(( __u64)(__be64)(*var))) + val)) & (__u64)0x00ff000000000000ULL) >> 40) | (((__u64)(((__builtin_constant_p((__u64)(( __u64)(__be64)(*var))) ?((__u64)((((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000000000ffULL) << 56) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000000000ff00ULL) << 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000000000ff0000ULL) << 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000ff000000ULL) << 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000ff00000000ULL) >> 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000ff0000000000ULL) >> 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00ff000000000000ULL) >> 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0xff00000000000000ULL) >> 56))) : __fswab64(( __u64)(__be64)(*var))) + val)) & (__u64)0xff00000000000000ULL) >> 56))) : __fswab64(((__builtin_constant_p((__u64)(( __u64)(__be64)(*var))) ?((__u64)((((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000000000ffULL) << 56) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000000000ff00ULL) << 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000000000ff0000ULL) << 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00000000ff000000ULL) << 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x000000ff00000000ULL) >> 8) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x0000ff0000000000ULL) >> 24) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0x00ff000000000000ULL) >> 40) |(((__u64)(( __u64)(__be64)(*var)) &(__u64)0xff00000000000000ULL) >> 56))) : __fswab64(( __u64)(__be64)(*var))) + val))));
}


#line 107 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/byteorder/little_endian.h" 2


#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/byteorder.h" 2


#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitops/le.h" 2



















 

















		

		

 





#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitops/ext2-non-atomic.h" 2
















#line 457 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bitops.h" 2






#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bitops/minix.h" 1















#line 464 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bitops.h" 2



#line 19 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/bitops.h" 2







static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
__inline__ __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
__inline__
#endif
 int get_bitmask_order(unsigned int count)
{
	int order;
	
	order = fls(count);
	return order;	/* We could be slightly more clever with -1 here... */
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
__inline__ __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
__inline__
#endif
 int get_count_order(unsigned int count)
{
	int order;
	
	order = fls(count) - 1;
	if (count & (count - 1))
		order++;
	return order;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long hweight_long(unsigned long w)
{
	return sizeof(w) == 4 ? hweight32(w) : hweight64(w);
}

/**
 * rol32 - rotate a 32-bit value left
 * @word: value to rotate
 * @shift: bits to roll
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u32 rol32(__u32 word, unsigned int shift)
{
	return (word << shift) | (word >> (32 - shift));
}

/**
 * ror32 - rotate a 32-bit value right
 * @word: value to rotate
 * @shift: bits to roll
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u32 ror32(__u32 word, unsigned int shift)
{
	return (word >> shift) | (word << (32 - shift));
}

/**
 * rol16 - rotate a 16-bit value left
 * @word: value to rotate
 * @shift: bits to roll
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u16 rol16(__u16 word, unsigned int shift)
{
	return (word << shift) | (word >> (16 - shift));
}

/**
 * ror16 - rotate a 16-bit value right
 * @word: value to rotate
 * @shift: bits to roll
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u16 ror16(__u16 word, unsigned int shift)
{
	return (word >> shift) | (word << (16 - shift));
}

/**
 * rol8 - rotate an 8-bit value left
 * @word: value to rotate
 * @shift: bits to roll
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u8 rol8(__u8 word, unsigned int shift)
{
	return (word << shift) | (word >> (8 - shift));
}

/**
 * ror8 - rotate an 8-bit value right
 * @word: value to rotate
 * @shift: bits to roll
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __u8 ror8(__u8 word, unsigned int shift)
{
	return (word >> shift) | (word << (8 - shift));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned fls_long(unsigned long l)
{
	if (sizeof(l) == 4)
		return fls(l);
	return fls64(l);
}

/**
 * __ffs64 - find first set bit in a 64 bit word
 * @word: The 64 bit word
 *
 * On 64 bit arches this is a synomyn for __ffs
 * The result is not defined if no bits are set, so check that @word
 * is non-zero before calling this.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long __ffs64(u64 word)
{
#if !((definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT)))
	if (((u32)word) == 0UL)
		return __ffs((u32)(word >> 32)) + 32;
#endif


	return __ffs((unsigned long)word);
}



/**
 * find_first_bit - find the first set bit in a memory region
 * @addr: The address to start the search at
 * @size: The maximum size to search
 *
 * Returns the bit number of the first set bit.
 */
extern unsigned long find_first_bit(const unsigned long *addr,
				    unsigned long size);

/**
 * find_first_zero_bit - find the first cleared bit in a memory region
 * @addr: The address to start the search at
 * @size: The maximum size to search
 *
 * Returns the bit number of the first cleared bit.
 */
extern unsigned long find_first_zero_bit(const unsigned long *addr,
					 unsigned long size);


/**
 * find_last_bit - find the last set bit in a memory region
 * @addr: The address to start the search at
 * @size: The maximum size to search
 *
 * Returns the bit number of the first set bit, or size.
 */
extern unsigned long find_last_bit(const unsigned long *addr,
				   unsigned long size);


/**
 * find_next_bit - find the next set bit in a memory region
 * @addr: The address to base the search on
 * @offset: The bitnumber to start searching at
 * @size: The bitmap size in bits
 */
extern unsigned long find_next_bit(const unsigned long *addr,
				   unsigned long size, unsigned long offset);

/**
 * find_next_zero_bit - find the next cleared bit in a memory region
 * @addr: The address to base the search on
 * @offset: The bitnumber to start searching at
 * @size: The bitmap size in bits
 */

extern unsigned long find_next_zero_bit(const unsigned long *addr,
					unsigned long size,
					unsigned long offset);




#line 57 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/thread_info.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/thread_info.h" 1
/* thread_info.h: low-level thread information
 *
 * Copyright (C) 2002  David Howells (dhowells@redhat.com)
 * - Incorporating suggestions made by Linus Torvalds and Dave Miller
 */




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 12 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/thread_info.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page.h" 2


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_types.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/const.h" 1
/* const.h: Macros for dealing with constants.  */




/* Some constant macros are used in both assembler and
 * C code.  Therefore we cannot annotate them always with
 * 'UL' and other type specifiers unilaterally.  We
 * use the following macros to deal with this.
 *
 * Similarly, _AT() will cast an expression with a type in C, but
 * leave it unchanged in asm.
 */




#if 1



#endif

#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_types.h" 2

/* PAGE_SHIFT determines the page size */







/* Cast PAGE_MASK to a signed type so that it is sign-extended if
   virtual addresses are 32-bits but physical addresses are larger
   (ie, 32-bit PAE). */


















#if definedEx(CONFIG_X86_64)
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_64_types.h" 1


























/*
 * Set __PAGE_OFFSET to the most negative possible address +
 * PGDIR_SIZE*16 (pgd slot 272).  The gap is to allow a space for a
 * hypervisor to fit.  Choosing 16 slots here is arbitrary, but it's
 * what Xen requires.
 */









/* See Documentation/x86/x86_64/mm.txt for a description of the memory map. */



/*
 * Kernel image size is limited to 512 MB (see level2_kernel_pgt in
 * arch/x86/kernel/head_64.S), and it is mapped here:
 */




void clear_page(void *page);
void copy_page(void *to, void *from);

/* duplicated to the one in bootmem.h */
extern unsigned long max_pfn;
extern unsigned long phys_base;

extern unsigned long __phys_addr(unsigned long);




extern void init_extra_mapping_uc(unsigned long phys, unsigned long size);
extern void init_extra_mapping_wb(unsigned long phys, unsigned long size);






#line 38 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_types.h" 2
#endif
#if !(definedEx(CONFIG_X86_64))
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_32_types.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/const.h" 1
/* const.h: Macros for dealing with constants.  */
















 




#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_32_types.h" 2

/*
 * This handles the memory map.
 *
 * A __PAGE_OFFSET of 0xC0000000 means that the kernel has
 * a virtual address space of one gigabyte, which limits the
 * amount of physical memory you can use to about 950MB.
 *
 * If you want more physical memory than this then see the CONFIG_HIGHMEM4G
 * and CONFIG_HIGHMEM64G options in the kernel configuration.
 */


#if definedEx(CONFIG_4KSTACKS)

#endif
#if !(definedEx(CONFIG_4KSTACKS))

#endif









#if definedEx(CONFIG_X86_PAE)
/* 44=32+12, the limit we can fit into an unsigned long pfn */



#endif
#if !(definedEx(CONFIG_X86_PAE))


#endif
/*
 * Kernel image size is limited to 512 MB (see in arch/x86/kernel/head_32.S)
 */



/*
 * This much address space is reserved for vmalloc() and iomap()
 * as well as fixmap mappings.
 */
extern unsigned int __VMALLOC_RESERVE;
extern int sysctl_legacy_va_layout;

extern void find_low_pfn_range(void);
extern void setup_bootmem_allocator(void);



#line 40 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_types.h" 2
#endif

extern int page_is_ram(unsigned long pagenr);
extern int devmem_is_allowed(unsigned long pagenr);

extern unsigned long max_low_pfn_mapped;
extern unsigned long max_pfn_mapped;

extern unsigned long init_memory_mapping(unsigned long start,
					 unsigned long end);

extern void initmem_init(unsigned long start_pfn, unsigned long end_pfn,
				int acpi, int k8);
extern void free_initmem(void);



#line 10 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page.h" 2

#if definedEx(CONFIG_X86_64)
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_64.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_64_types.h" 1









































































#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_64.h" 2


#line 13 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page.h" 2
#endif
#if !(definedEx(CONFIG_X86_64))
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_32.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_32_types.h" 1



















 















 




















#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_32.h" 2


#if definedEx(CONFIG_HUGETLB_PAGE)

#endif

#if definedEx(CONFIG_DEBUG_VIRTUAL)
extern unsigned long __phys_addr(unsigned long);
#endif
#if !(definedEx(CONFIG_DEBUG_VIRTUAL))

#endif





#if definedEx(CONFIG_X86_USE_3DNOW)
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mmx.h" 1



/*
 *	MMX 3Dnow! helper operations
 */

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 10 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mmx.h" 2

extern void *_mmx_memcpy(void *to, const void *from, size_t size);
extern void mmx_clear_page(void *page);
extern void mmx_copy_page(void *to, void *from);


#line 27 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_32.h" 2

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void clear_page(void *page)
{
	mmx_clear_page(page);
}







//START PROBLEM SECTION with (!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_X86_USE_3DNOW))

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void copy_page(void *to, void *from)
{
	mmx_copy_page(to, from);
}
#endif
#if !(definedEx(CONFIG_X86_USE_3DNOW))

#if 1



typedef __builtin_va_list __gnuc_va_list;


#if (!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)))


#if 1

typedef __gnuc_va_list va_list;



#endif
#endif


extern char *strndup_user(const char  *, long);
extern void *memdup_user(const void  *, size_t);



extern char *strcpy(char *dest, const char *src);


extern char *strncpy(char *dest, const char *src, size_t count);


extern char *strcat(char *dest, const char *src);


extern char *strncat(char *dest, const char *src, size_t count);


extern int strcmp(const char *cs, const char *ct);


extern int strncmp(const char *cs, const char *ct, size_t count);


extern char *strchr(const char *s, int c);


extern size_t strlen(const char *s);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline)) void *__memcpy(void *to, const void *from, size_t n)
{
	int d0, d1, d2;
	asm volatile("rep ; movsl\n\t"
		     "movl %4,%%ecx\n\t"
		     "andl $3,%%ecx\n\t"
		     "jz 1f\n\t"
		     "rep ; movsb\n\t"
		     "1:"
		     : "=&c" (d0), "=&D" (d1), "=&S" (d2)
		     : "0" (n / 4), "g" (n), "1" ((long)to), "2" ((long)from)
		     : "memory");
	return to;
}

/*
 * This looks ugly, but the compiler can optimize it totally,
 * as the count is constant.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline)) void *__constant_memcpy(void *to, const void *from,
					       size_t n)
{
	long esi, edi;
	if (!n)
		return to;

	switch (n) {
	case 1:
		*(char *)to = *(char *)from;
		return to;
	case 2:
		*(short *)to = *(short *)from;
		return to;
	case 4:
		*(int *)to = *(int *)from;
		return to;
	case 3:
		*(short *)to = *(short *)from;
		*((char *)to + 2) = *((char *)from + 2);
		return to;
	case 5:
		*(int *)to = *(int *)from;
		*((char *)to + 4) = *((char *)from + 4);
		return to;
	case 6:
		*(int *)to = *(int *)from;
		*((short *)to + 2) = *((short *)from + 2);
		return to;
	case 8:
		*(int *)to = *(int *)from;
		*((int *)to + 1) = *((int *)from + 1);
		return to;
	}

	esi = (long)from;
	edi = (long)to;
	if (n >= 5 * 4) {
		/* large block: use rep prefix */
		int ecx;
		asm volatile("rep ; movsl"
			     : "=&c" (ecx), "=&D" (edi), "=&S" (esi)
			     : "0" (n / 4), "1" (edi), "2" (esi)
			     : "memory"
		);
	} else {
		/* small block: don't clobber ecx + smaller code */
		if (n >= 4 * 4)
			asm volatile("movsl"
				     : "=&D"(edi), "=&S"(esi)
				     : "0"(edi), "1"(esi)
				     : "memory");
		if (n >= 3 * 4)
			asm volatile("movsl"
				     : "=&D"(edi), "=&S"(esi)
				     : "0"(edi), "1"(esi)
				     : "memory");
		if (n >= 2 * 4)
			asm volatile("movsl"
				     : "=&D"(edi), "=&S"(esi)
				     : "0"(edi), "1"(esi)
				     : "memory");
		if (n >= 1 * 4)
			asm volatile("movsl"
				     : "=&D"(edi), "=&S"(esi)
				     : "0"(edi), "1"(esi)
				     : "memory");
	}
	switch (n % 4) {
		/* tail */
	case 0:
		return to;
	case 1:
		asm volatile("movsb"
			     : "=&D"(edi), "=&S"(esi)
			     : "0"(edi), "1"(esi)
			     : "memory");
		return to;
	case 2:
		asm volatile("movsw"
			     : "=&D"(edi), "=&S"(esi)
			     : "0"(edi), "1"(esi)
			     : "memory");
		return to;
	default:
		asm volatile("movsw\n\tmovsb"
			     : "=&D"(edi), "=&S"(esi)
			     : "0"(edi), "1"(esi)
			     : "memory");
		return to;
	}
}












	
		
	




	
		
	







#if !(definedEx(CONFIG_X86_USE_3DNOW))
/*
 *	No 3D Now!
 */

#if !(definedEx(CONFIG_KMEMCHECK))


 




#endif
#if definedEx(CONFIG_KMEMCHECK)
/*
 * kmemcheck becomes very happy if we use the REP instructions unconditionally,
 * because it means that we know both memory operands in advance.
 */

#endif
#endif

void *memmove(void *dest, const void *src, size_t n);




extern void *memchr(const void *cs, int c, size_t count);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void *__memset_generic(void *s, char c, size_t count)
{
	int d0, d1;
	asm volatile("rep\n\t"
		     "stosb"
		     : "=&c" (d0), "=&D" (d1)
		     : "a" (c), "1" (s), "0" (count)
		     : "memory");
	return s;
}

/* we might want to write optimized versions of these later */


/*
 * memset(x, 0, y) is a reasonably common thing to do, so we want to fill
 * things 32 bits at a time even when we don't know the size of the
 * area at compile-time..
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline))
void *__constant_c_memset(void *s, unsigned long c, size_t count)
{
	int d0, d1;
	asm volatile("rep ; stosl\n\t"
		     "testb $2,%b3\n\t"
		     "je 1f\n\t"
		     "stosw\n"
		     "1:\ttestb $1,%b3\n\t"
		     "je 2f\n\t"
		     "stosb\n"
		     "2:"
		     : "=&c" (d0), "=&D" (d1)
		     : "a" (c), "q" (count), "0" (count/4), "1" ((long)s)
		     : "memory");
	return s;
}

/* Added by Gertjan van Wingerde to make minix and sysv module work */

extern size_t strnlen(const char *s, size_t count);
/* end of additional stuff */


extern char *strstr(const char *cs, const char *ct);

/*
 * This looks horribly ugly, but the compiler can optimize it totally,
 * as we by now know that both pattern and count is constant..
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline))
void *__constant_c_and_count_memset(void *s, unsigned long pattern,
				    size_t count)
{
	switch (count) {
	case 0:
		return s;
	case 1:
		*(unsigned char *)s = pattern & 0xff;
		return s;
	case 2:
		*(unsigned short *)s = pattern & 0xffff;
		return s;
	case 3:
		*(unsigned short *)s = pattern & 0xffff;
		*((unsigned char *)s + 2) = pattern & 0xff;
		return s;
	case 4:
		*(unsigned long *)s = pattern;
		return s;
	}








	{
		int d0, d1;

		
		
#if 1
		unsigned long eax = pattern;
#endif
		switch (count % 4) {
		case 0:
			asm volatile("rep ; stosl" "" : "=&c" (d0), "=&D" (d1) : "a" (eax), "0" (count/4), "1" ((long)s) : "memory");
			return s;
		case 1:
			asm volatile("rep ; stosl" "\n\tstosb" : "=&c" (d0), "=&D" (d1) : "a" (eax), "0" (count/4), "1" ((long)s) : "memory");
			return s;
		case 2:
			asm volatile("rep ; stosl" "\n\tstosw" : "=&c" (d0), "=&D" (d1) : "a" (eax), "0" (count/4), "1" ((long)s) : "memory");
			return s;
		default:
			asm volatile("rep ; stosl" "\n\tstosw\n\tstosb" : "=&c" (d0), "=&D" (d1) : "a" (eax), "0" (count/4), "1" ((long)s) : "memory");
			return s;
		}
	}


}














 





/*
 * find the first occurrence of byte 'c', or 1 past the area if none
 */

extern void *memscan(void *addr, int c, size_t size);



#line 4 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/string.h" 2
 

#line 23 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/string.h" 2








size_t strlcpy(char *, const char *, size_t);








extern size_t strlcat(char *, const char *, __kernel_size_t);








extern int strnicmp(const char *, const char *, __kernel_size_t);


extern int strcasecmp(const char *s1, const char *s2);


extern int strncasecmp(const char *s1, const char *s2, size_t n);





extern char * strnchr(const char *, size_t, int);


extern char * strrchr(const char *,int);

extern char * 
#if (definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))
__attribute__((warn_unused_result))
#endif
#if (!(definedEx(CONFIG_ENABLE_MUST_CHECK)) && !((definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))))

#endif
 skip_spaces(const char *);

extern char *strim(char *);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 
#if (definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))
__attribute__((warn_unused_result))
#endif
#if (!(definedEx(CONFIG_ENABLE_MUST_CHECK)) && !((definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))))

#endif
 char *strstrip(char *str)
{
	return strim(str);
}





extern char * strnstr(const char *, const char *, size_t);








extern char * strpbrk(const char *,const char *);


extern char * strsep(char **,const char *);


extern __kernel_size_t strspn(const char *,const char *);


extern __kernel_size_t strcspn(const char *,const char *);














extern int __builtin_memcmp(const void *,const void *,__kernel_size_t);




extern char *kstrdup(const char *s, gfp_t gfp);
extern char *kstrndup(const char *s, size_t len, gfp_t gfp);
extern void *kmemdup(const void *src, size_t len, gfp_t gfp);

extern char **argv_split(gfp_t gfp, const char *str, int *argcp);
extern void argv_free(char **argv);

extern bool sysfs_streq(const char *s1, const char *s2);

#if definedEx(CONFIG_BINARY_PRINTF)
int vbin_printf(u32 *bin_buf, size_t size, const char *fmt, va_list args);
int bstr_printf(char *buf, size_t size, const char *fmt, const u32 *bin_buf);
int bprintf(u32 *bin_buf, size_t size, const char *fmt, ...) __attribute__((format(printf,3,4)));
#endif
extern ssize_t memory_read_from_buffer(void *to, size_t count, loff_t *ppos,
			const void *from, size_t available);

/**
 * strstarts - does @str start with @prefix?
 * @str: string to examine
 * @prefix: prefix to look for.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 bool strstarts(const char *str, const char *prefix)
{
	return strncmp(str, prefix, strlen(prefix)) == 0;
}
#endif

#line 39 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_32.h" 2

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void clear_page(void *page)
{
	__builtin_memset(page, 0, ((1UL) << 12));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void copy_page(void *to, void *from)
{
	
#if (!(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_KMEMCHECK)) && !((definedEx(CONFIG_KMEMCHECK) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)))))
__builtin_memcpy(to, from, ((1UL) << 12))
#endif
#if (definedEx(CONFIG_KMEMCHECK) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !((!(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_KMEMCHECK)) && !((definedEx(CONFIG_KMEMCHECK) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)))))))
__memcpy((to), (from), (((1UL) << 12)))
#endif
;
}
#endif


#line 15 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page.h" 2
#endif

struct page;

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void clear_user_page(void *page, unsigned long vaddr,
				   struct page *pg)
{
	clear_page(page);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void copy_user_page(void *to, void *from, unsigned long vaddr,
				  struct page *topage)
{
	copy_page(to, from);
}







/* __pa_symbol should be used for C visible symbols.
   This seems to be the official gcc blessed way to do such arithmetic. */







/*
 * virt_to_page(kaddr) returns a valid pointer if and only if
 * virt_addr_valid(kaddr) returns true.
 */


extern bool __virt_addr_valid(unsigned long kaddr);



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/memory_model.h" 1








 







/*
 * supports 3 memory models.
 */




 












 



 




















#line 60 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/getorder.h" 1




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/getorder.h" 2

/* Pure 2^n version of get_order */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((__const__)) int get_order(unsigned long size)
{
	int order;

	size = (size - 1) >> (12 - 1);
	order = -1;
	do {
		size >>= 1;
		order++;
	} while (size);
	return order;
}



#line 61 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page.h" 2





#line 13 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/thread_info.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/types.h" 1













 




#line 14 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/thread_info.h" 2

/*
 * low level task data that entry.S needs immediate access to
 * - this struct should fit entirely inside of one cache line
 * - this struct shares the supervisor stack pages
 */

struct task_struct;
struct exec_domain;
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor-flags.h" 1


/* Various flags defined: can be included from assembler. */

/*
 * EFLAGS bits
 */


















/*
 * Basic CPU control in CR0
 */












/*
 * Paging options in CR3
 */



/*
 * Intel CPU features in CR4
 */














/*
 * x86-64 Task Priority Register, CR8
 */


/*
 * AMD and Transmeta use MSRs for configuration; see <asm/msr-index.h>
 */

/*
 *      NSC/Cyrix CPU configuration register indexes
 */



















 



#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 2

/* Forward declaration, a strange C thing */
struct task_struct;
struct mm_struct;

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/vm86.h" 1



/*
 * I'm guessing at the VIF/VIP flag usage, but hope that this is how
 * the Pentium uses them. Linux will return from vm86 mode when both
 * VIF and VIP is set.
 *
 * On a Pentium, we could probably optimize the virtual flags directly
 * in the eflags register instead of doing it "by hand" in vflags...
 *
 * Linus
 */

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor-flags.h" 1






























































































 



#line 17 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/vm86.h" 2










/*
 * Return values for the 'vm86()' system call
 */








/*
 * Additional return values when invoking new vm86()
 */



/*
 * function codes when invoking new vm86()
 */








/*
 * This is the stack-layout seen by the user space program when we have
 * done a translation of "SAVE_ALL" from vm86 mode. The real kernel layout
 * is 'kernel_vm86_regs' (see below).
 */

struct vm86_regs {
/*
 * normal regs, with special meaning for the segment descriptors..
 */
	long ebx;
	long ecx;
	long edx;
	long esi;
	long edi;
	long ebp;
	long eax;
	long __null_ds;
	long __null_es;
	long __null_fs;
	long __null_gs;
	long orig_eax;
	long eip;
	unsigned short cs, __csh;
	long eflags;
	long esp;
	unsigned short ss, __ssh;
/*
 * these are specific to v86 mode:
 */
	unsigned short es, __esh;
	unsigned short ds, __dsh;
	unsigned short fs, __fsh;
	unsigned short gs, __gsh;
};

struct revectored_struct {
	unsigned long __map[8];			/* 256 bits */
};

struct vm86_struct {
	struct vm86_regs regs;
	unsigned long flags;
	unsigned long screen_bitmap;
	unsigned long cpu_type;
	struct revectored_struct int_revectored;
	struct revectored_struct int21_revectored;
};

/*
 * flags masks
 */


struct vm86plus_info_struct {
	unsigned long force_return_for_pic:1;
	unsigned long vm86dbg_active:1;       /* for debugger */
	unsigned long vm86dbg_TFpendig:1;     /* for debugger */
	unsigned long unused:28;
	unsigned long is_vm86pus:1;	      /* for vm86 internal use */
	unsigned char vm86dbg_intxxtab[32];   /* for debugger */
};
struct vm86plus_struct {
	struct vm86_regs regs;
	unsigned long flags;
	unsigned long screen_bitmap;
	unsigned long cpu_type;
	struct revectored_struct int_revectored;
	struct revectored_struct int21_revectored;
	struct vm86plus_info_struct vm86plus;
};


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/ptrace.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/ptrace.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/ptrace-abi.h" 1























#if 1



























/* top of stack page */


#endif
/* Arbitrarily choose the same ptrace numbers as used by the Sparc code. */









/* only useful for access 32bit programs / kernels */












#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 86 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/ptrace-abi.h" 2

/* configuration/status structure used in PTRACE_BTS_CONFIG and
   PTRACE_BTS_STATUS commands.
*/
struct ptrace_bts_config {
	/* requested or actual size of BTS buffer in bytes */
	__u32 size;
	/* bitmask of below flags */
	__u32 flags;
	/* buffer overflow signal */
	__u32 signal;
	/* actual size of bts_struct in bytes */
	__u32 bts_size;
};







/* Configure branch trace recording.
   ADDR points to a struct ptrace_bts_config.
   DATA gives the size of that buffer.
   A new buffer is allocated, if requested in the flags.
   An overflow signal may only be requested for new buffers.
   Returns the number of bytes read.
*/

/* Return the current configuration in a struct ptrace_bts_config
   pointed to by ADDR; DATA gives the size of that buffer.
   Returns the number of bytes written.
*/

/* Return the number of available BTS records for draining.
   DATA and ADDR are ignored.
*/

/* Get a single BTS record.
   DATA defines the index into the BTS array, where 0 is the newest
   entry, and higher indices refer to older entries.
   ADDR is pointing to struct bts_struct (see asm/ds.h).
*/

/* Clear the BTS buffer.
   DATA and ADDR are ignored.
*/

/* Read all available BTS records and clear the buffer.
   ADDR points to an array of struct bts_struct.
   DATA gives the size of that buffer.
   BTS records are read from oldest to newest.
   Returns number of BTS records drained.
*/


#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/ptrace.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor-flags.h" 1






























































































 



#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/ptrace.h" 2


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/segment.h" 1



/* Constructor for a conventional segment GDT (or LDT) entry */
/* This is a macro so it can be used in initializers */







/* Simple and small GDT entries for booting only */











/*
 * The layout of the per-CPU GDT under Linux:
 *
 *   0 - null
 *   1 - reserved
 *   2 - reserved
 *   3 - reserved
 *
 *   4 - unused			<==== new cacheline
 *   5 - unused
 *
 *  ------- start of TLS (Thread-Local Storage) segments:
 *
 *   6 - TLS segment #1			[ glibc's TLS segment ]
 *   7 - TLS segment #2			[ Wine's %fs Win32 segment ]
 *   8 - TLS segment #3
 *   9 - reserved
 *  10 - reserved
 *  11 - reserved
 *
 *  ------- start of kernel segments:
 *
 *  12 - kernel code segment		<==== new cacheline
 *  13 - kernel data segment
 *  14 - default user CS
 *  15 - default user DS
 *  16 - TSS
 *  17 - LDT
 *  18 - PNPBIOS support (16->32 gate)
 *  19 - PNPBIOS support
 *  20 - PNPBIOS support
 *  21 - PNPBIOS support
 *  22 - PNPBIOS support
 *  23 - APM BIOS support
 *  24 - APM BIOS support
 *  25 - APM BIOS support
 *
 *  26 - ESPFIX small SS
 *  27 - per-cpu			[ offset to per-cpu data area ]
 *  28 - stack_canary-20		[ for stack protector ]
 *  29 - unused
 *  30 - unused
 *  31 - TSS for double fault handler
 */























#if definedEx(CONFIG_SMP)

#endif
#if !(definedEx(CONFIG_SMP))

#endif

#if definedEx(CONFIG_CC_STACKPROTECTOR)

#endif
#if !(definedEx(CONFIG_CC_STACKPROTECTOR))

#endif


/*
 * The GDT has 32 entries
 */


/* The PnP BIOS entries in the GDT */






/* The PnP BIOS selectors */






/* Bottom two bits of selector give the ring privilege level */

/* Bit 2 is table indicator (LDT/GDT) */


/* User mode is privilege level 3 */

/* LDT segment has TI set, GDT has it cleared */



/*
 * Matching rules for certain types of segments.
 */

/* Matches PNP_CS32 and PNP_CS16 (they must be consecutive) */



 












































/* User mode is privilege level 3 */

/* LDT segment has TI set, GDT has it cleared */



/* Bottom two bits of selector give the ring privilege level */

/* Bit 2 is table indicator (LDT/GDT) */










extern const char early_idt_handlers[32][10];



#line 11 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/ptrace.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_types.h" 1




































 









					 


				




#line 12 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/ptrace.h" 2








	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



#if 1


	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	


	
	
	
	
	



#if 1
struct pt_regs {
	unsigned long r15;
	unsigned long r14;
	unsigned long r13;
	unsigned long r12;
	unsigned long bp;
	unsigned long bx;
/* arguments: non interrupts/non tracing syscalls only save upto here*/
	unsigned long r11;
	unsigned long r10;
	unsigned long r9;
	unsigned long r8;
	unsigned long ax;
	unsigned long cx;
	unsigned long dx;
	unsigned long si;
	unsigned long di;
	unsigned long orig_ax;
/* end of arguments */
/* cpu exception frame or undefined */
	unsigned long ip;
	unsigned long cs;
	unsigned long flags;
	unsigned long sp;
	unsigned long ss;
/* top of stack page */
};

#endif
#endif

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/init.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/init.h" 2

/* These macros are used to mark some functions or 
 * initialized data (doesn't apply to uninitialized data)
 * as `initialization' functions. The kernel can take this
 * as hint that the function is used only during the initialization
 * phase and free up used memory resources after
 *
 * Usage:
 * For functions:
 * 
 * You should add __init immediately before the function name, like:
 *
 * static void __init initme(int x, int y)
 * {
 *    extern int z; z = x * y;
 * }
 *
 * If the function has a prototype somewhere, you can also add
 * __init between closing brace of the prototype and semicolon:
 *
 * extern int initialize_foobar_device(int, int, int) __init;
 *
 * For initialized data:
 * You should insert __initdata between the variable name and equal
 * sign followed by value, e.g.:
 *
 * static int init_variable __initdata = 0;
 * static const char linux_logo[] __initconst = { 0x32, 0x36, ... };
 *
 * Don't forget to initialize data not at file scope, i.e. within a function,
 * as gcc otherwise puts the data into the bss section and not into the init
 * section.
 * 
 * Also note, that this data cannot be "const".
 */

/* These are for everybody (although not all archs will actually
   discard it in modules) */






/* modpost check for section mismatches during the kernel build.
 * A section mismatch happens when there are references from a
 * code or data section to an init section (both code or data).
 * The init sections are (for most archs) discarded by the kernel
 * when early init has completed so all such references are potential bugs.
 * For exit sections the same issue exists.
 * The following markers are used for the cases where the reference to
 * the *init / *exit section (code or data) is valid and will teach
 * modpost not to issue a warning.
 * The markers follow same syntax rules as __init / __initdata. */




/* compatibility defines */







#if 1

#endif


/* Used for HOTPLUG */







/* Used for HOTPLUG_CPU */







/* Used for MEMORY_HOTPLUG */







/* For assembly routines */




















/* silence warnings when references are OK */





/*
 * Used for initialization calls..
 */
typedef int (*initcall_t)(void);
typedef void (*exitcall_t)(void);

extern initcall_t __con_initcall_start[], __con_initcall_end[];
extern initcall_t __security_initcall_start[], __security_initcall_end[];

/* Used for contructor calls. */
typedef void (*ctor_fn_t)(void);

/* Defined in init/main.c */
extern int do_one_initcall(initcall_t fn);
extern char __attribute__ ((__section__(".init.data"))) boot_command_line[];
extern char *saved_command_line;
extern unsigned int reset_devices;

/* used by init/main.c */
void setup_arch(char **);
void prepare_namespace(void);

extern void (*late_time_init)(void);

extern int initcall_debug;


  


/* initcalls are now grouped by functionality into separate 
 * subsections. Ordering inside the subsections is determined
 * by link order. 
 * For backwards compatibility, initcall() puts the call in 
 * the device init subsection.
 *
 * The `id' arg to __define_initcall() is needed so that multiple initcalls
 * can point at the same handler without causing duplicate-symbol build errors.
 */





/*
 * Early initcalls run before initializing SMP.
 *
 * Only for built-in code, not modules.
 */


/*
 * A "pure" initcall has no dependencies on anything else, and purely
 * initializes variables that couldn't be statically initialized.
 *
 * This only exists for built-in code, not for modules.
 */































struct obs_kernel_param {
	const char *str;
	int (*setup_func)(char *);
	int early;
};

/*
 * Only for really core code.  See moduleparam.h for the normal way.
 *
 * Force the alignment so the compiler doesn't space elements of the
 * obs_kernel_param "array" too far apart in .init.setup.
 */











/* NOTE: fn is as per module_param, not __setup!  Emits warning if fn
 * returns non-zero. */



/* Relies on boot_command_line being set */
void __attribute__ ((__section__(".init.text")))  __attribute__((no_instrument_function)) parse_early_param(void);
void __attribute__ ((__section__(".init.text")))  __attribute__((no_instrument_function)) parse_early_options(char *cmdline);

/**
 * module_init() - driver initialization entry point
 * @x: function to be run at kernel boot time or module insertion
 * 
 * module_init() will either be called during do_initcalls() (if
 * builtin) or at module insertion time (if a module).  There can only
 * be one per module.
 */


/**
 * module_exit() - driver exit entry point
 * @x: function to be run when driver is removed
 * 
 * module_exit() will wrap the driver clean-up code
 * with cleanup_module() when used with rmmod when
 * the driver is a module.  If the driver is statically
 * compiled into the kernel, module_exit() has no effect.
 * There can only be one per module.
 */


 


























/* Data marked not to be saved by software suspend */


/* This means "can be init if no module support, otherwise module load
   may call it." */
#if definedEx(CONFIG_MODULES)






#endif
#if !(definedEx(CONFIG_MODULES))






#endif
/* Functions marked as __devexit may be discarded at kernel link time, depending
   on config options.  Newer versions of binutils detect references from
   retained sections to discarded sections and flag an error.  Pointers to
   __devexit functions must use __devexit_p(function_name), the wrapper will
   insert either the function_name or NULL, depending on the config options.
 */


 



#if 1

#endif

#line 135 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/ptrace.h" 2

struct cpuinfo_x86;
struct task_struct;

extern unsigned long profile_pc(struct pt_regs *regs);

extern unsigned long
convert_ip_to_linear(struct task_struct *child, struct pt_regs *regs);
extern void send_sigtrap(struct task_struct *tsk, struct pt_regs *regs,
			 int error_code, int si_code);
void signal_fault(struct pt_regs *regs, void  *frame, char *where);

extern long syscall_trace_enter(struct pt_regs *);
extern void syscall_trace_leave(struct pt_regs *);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long regs_return_value(struct pt_regs *regs)
{
	return regs->ax;
}

/*
 * user_mode_vm(regs) determines whether a register set came from user mode.
 * This is true if V8086 mode was enabled OR if the register set was from
 * protected mode with RPL-3 CS value.  This tricky test checks that with
 * one comparison.  Many places in the kernel can bypass this full check
 * if they have already ruled out V8086 mode, so user_mode(regs) can be used.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int user_mode(struct pt_regs *regs)
{

	return (regs->cs & 0x3) == 0x3;
 	

}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int user_mode_vm(struct pt_regs *regs)
{

	return ((regs->cs & 0x3) | (regs->flags & 0x00020000)) >=
		0x3;
 	

}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int v8086_mode(struct pt_regs *regs)
{

	return (regs->flags & 0x00020000);
 	

}

/*
 * X86_32 CPUs don't save ss and esp if the CPU is already in kernel mode
 * when it traps.  The previous stack will be directly underneath the saved
 * registers, and 'sp/ss' won't even have been saved. Thus the '&regs->sp'.
 *
 * This is valid only for kernel mode traps.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long kernel_stack_pointer(struct pt_regs *regs)
{

	return (unsigned long)(&regs->sp);
 	

}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long instruction_pointer(struct pt_regs *regs)
{
	return regs->ip;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long frame_pointer(struct pt_regs *regs)
{
	return regs->bp;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long user_stack_pointer(struct pt_regs *regs)
{
	return regs->sp;
}

/* Query offset/name of register from its name/offset */
extern int regs_query_register_offset(const char *name);
extern const char *regs_query_register_name(unsigned int offset);


/**
 * regs_get_register() - get register value from its offset
 * @regs:	pt_regs from which register value is gotten.
 * @offset:	offset number of the register.
 *
 * regs_get_register returns the value of a register. The @offset is the
 * offset of the register in struct pt_regs address which specified by @regs.
 * If @offset is bigger than MAX_REG_OFFSET, this returns 0.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long regs_get_register(struct pt_regs *regs,
					      unsigned int offset)
{
	if (__builtin_expect(!!(offset > (((size_t) &((struct pt_regs *)0)->ss))), 0))
		return 0;
	return *(unsigned long *)((unsigned long)regs + offset);
}

/**
 * regs_within_kernel_stack() - check the address in the stack
 * @regs:	pt_regs which contains kernel stack pointer.
 * @addr:	address which is checked.
 *
 * regs_within_kernel_stack() checks @addr is within the kernel stack page(s).
 * If @addr is within the kernel stack, it returns true. If not, returns false.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int regs_within_kernel_stack(struct pt_regs *regs,
					   unsigned long addr)
{
	return ((addr & ~((((1UL) << 12) << 
#if ((definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_4KSTACKS)))) || (!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_4KSTACKS))))
1
#endif
#if (!((!(definedEx(CONFIG_4KSTACKS)) && !(definedEx(CONFIG_X86_64)))) && definedEx(CONFIG_4KSTACKS) && !(definedEx(CONFIG_X86_64)) && !(((definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_4KSTACKS)))) || (!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_4KSTACKS))))))
0
#endif
) - 1))  ==
		(kernel_stack_pointer(regs) & ~((((1UL) << 12) << 
#if ((definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_4KSTACKS)))) || (!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_4KSTACKS))))
1
#endif
#if (!((!(definedEx(CONFIG_4KSTACKS)) && !(definedEx(CONFIG_X86_64)))) && definedEx(CONFIG_4KSTACKS) && !(definedEx(CONFIG_X86_64)) && !(((definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_4KSTACKS)))) || (!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_4KSTACKS))))))
0
#endif
) - 1)));
}

/**
 * regs_get_kernel_stack_nth() - get Nth entry of the stack
 * @regs:	pt_regs which contains kernel stack pointer.
 * @n:		stack entry number.
 *
 * regs_get_kernel_stack_nth() returns @n th entry of the kernel stack which
 * is specified by @regs. If the @n th entry is NOT in the kernel stack,
 * this returns 0.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long regs_get_kernel_stack_nth(struct pt_regs *regs,
						      unsigned int n)
{
	unsigned long *addr = (unsigned long *)kernel_stack_pointer(regs);
	addr += n;
	if (regs_within_kernel_stack(regs, (unsigned long)addr))
		return *addr;
	else
		return 0;
}

/* Get Nth argument at function call */
extern unsigned long regs_get_argument_nth(struct pt_regs *regs,
					   unsigned int n);

/*
 * These are defined as per linux/ptrace.h, which see.
 */

extern void user_enable_single_step(struct task_struct *);
extern void user_disable_single_step(struct task_struct *);

extern void user_enable_block_step(struct task_struct *);


 



struct user_desc;
extern int do_get_thread_area(struct task_struct *p, int idx,
			      struct user_desc  *info);
extern int do_set_thread_area(struct task_struct *p, int idx,
			      struct user_desc  *info, int can_allocate);

#if definedEx(CONFIG_X86_PTRACE_BTS)
extern void ptrace_bts_untrace(struct task_struct *tsk);


#endif



#line 132 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/vm86.h" 2

/*
 * This is the (kernel) stack-layout when we have done a "SAVE_ALL" from vm86
 * mode - the main change is that the old segment descriptors aren't
 * useful any more and are forced to be zero by the kernel (and the
 * hardware when a trap occurs), and the real segment descriptors are
 * at the end of the structure. Look at ptrace.h to see the "normal"
 * setup. For user space layout see 'struct vm86_regs' above.
 */

struct kernel_vm86_regs {
/*
 * normal regs, with special meaning for the segment descriptors..
 */
	struct pt_regs pt;
/*
 * these are specific to v86 mode:
 */
	unsigned short es, __esh;
	unsigned short ds, __dsh;
	unsigned short fs, __fsh;
	unsigned short gs, __gsh;
};

struct kernel_vm86_struct {
	struct kernel_vm86_regs regs;
/*
 * the below part remains on the kernel stack while we are in VM86 mode.
 * 'tss.esp0' then contains the address of VM86_TSS_ESP0 below, and when we
 * get forced back from VM86, the CPU and "SAVE_ALL" will restore the above
 * 'struct kernel_vm86_regs' with the then actual values.
 * Therefore, pt_regs in fact points to a complete 'kernel_vm86_struct'
 * in kernelspace, hence we need not reget the data from userspace.
 */

	unsigned long flags;
	unsigned long screen_bitmap;
	unsigned long cpu_type;
	struct revectored_struct int_revectored;
	struct revectored_struct int21_revectored;
	struct vm86plus_info_struct vm86plus;
	struct pt_regs *regs32;   /* here we save the pointer to the old regs */
/*
 * The below is not part of the structure, but the stack layout continues
 * this way. In front of 'return-eip' may be some data, depending on
 * compilation, so we don't rely on this and save the pointer to 'oldregs'
 * in 'regs32' above.
 * However, with GCC-2.7.2 and the current CFLAGS you see exactly this:

	long return-eip;        from call to vm86()
	struct pt_regs oldregs;  user space registers as saved by syscall
 */
};


void handle_vm86_fault(struct kernel_vm86_regs *, long);
int handle_vm86_trap(struct kernel_vm86_regs *, long, int);
struct pt_regs *save_v86_state(struct kernel_vm86_regs *);

struct task_struct;
void release_vm86_irqs(struct task_struct *);

 




	





#line 12 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/math_emu.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/ptrace.h" 1


















	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



 

	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	


	
	
	
	
	



 
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	


	
	
	
	
	
















			 







	












	
 	






	
		
 	






	
 	













	
 	





	




	




	

















					      

	
		
	











					   

	
		












						      

	
	
	
		
	
		




					   











 





			      

			      









#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/math_emu.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/vm86.h" 1































































	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



	
	
	
	



	



	
	
	
	
	
	








	
	
	
	
	
	


	
	
	
	
	
	
	


















	



	
	
	
	



	









	
	
	
	
	
	
	




















 




	





#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/math_emu.h" 2

/* This structure matches the layout of the data saved to the stack
   following a device-not-present interrupt, part of it saved
   automatically by the 80386/80486.
   */
struct math_emu_info {
	long ___orig_eip;
	union {
		struct pt_regs *regs;
		struct kernel_vm86_regs *vm86;
	};
};

#line 13 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/segment.h" 1





























































































 




 









































 



































































#line 14 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/types.h" 1













 




#line 15 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/sigcontext.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/sigcontext.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/sigcontext.h" 2





/*
 * bytes 464..511 in the current 512byte layout of fxsave/fxrstor frame
 * are reserved for SW usage. On cpu's supporting xsave/xrstor, these bytes
 * are used to extended the fpstate pointer in the sigcontext, which now
 * includes the extended state information along with fpstate information.
 *
 * Presence of FP_XSTATE_MAGIC1 at the beginning of this SW reserved
 * area and FP_XSTATE_MAGIC2 at the end of memory layout
 * (extended_size - FP_XSTATE_MAGIC2_SIZE) indicates the presence of the
 * extended state information in the memory layout pointed by the fpstate
 * pointer in sigcontext.
 */
struct _fpx_sw_bytes {
	__u32 magic1;		/* FP_XSTATE_MAGIC1 */
	__u32 extended_size;	/* total size of the layout referred by
				 * fpstate pointer in the sigcontext.
				 */
	__u64 xstate_bv;
				/* feature bit mask (including fp/sse/extended
				 * state) that is present in the memory
				 * layout.
				 */
	__u32 xstate_size;	/* actual xsave state size, based on the
				 * features saved in the layout.
				 * 'extended_size' will be greater than
				 * 'xstate_size'.
				 */
	__u32 padding[7];	/*  for future use. */
};

















	
	



	
	
	



	



	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	

	
		
		
	






	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	






	
	
	

 



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


#if 1
/* FXSAVE frame */
/* Note: reserved1/2 may someday contain valuable data. Always save/restore
   them when you change signal frames. */
struct _fpstate {
	__u16	cwd;
	__u16	swd;
	__u16	twd;		/* Note this is not the same as the
				   32bit/x87/FSAVE twd */
	__u16	fop;
	__u64	rip;
	__u64	rdp;
	__u32	mxcsr;
	__u32	mxcsr_mask;
	__u32	st_space[32];	/* 8*16 bytes for each FP-reg */
	__u32	xmm_space[64];	/* 16*16 bytes for each XMM-reg  */
	__u32	reserved2[12];
	union {
		__u32	reserved3[12];
		struct _fpx_sw_bytes sw_reserved; /* represents the extended
						   * state information */
	};
};


struct sigcontext {
	unsigned long r8;
	unsigned long r9;
	unsigned long r10;
	unsigned long r11;
	unsigned long r12;
	unsigned long r13;
	unsigned long r14;
	unsigned long r15;
	unsigned long di;
	unsigned long si;
	unsigned long bp;
	unsigned long bx;
	unsigned long dx;
	unsigned long ax;
	unsigned long cx;
	unsigned long sp;
	unsigned long ip;
	unsigned long flags;
	unsigned short cs;
	unsigned short gs;
	unsigned short fs;
	unsigned short __pad0;
	unsigned long err;
	unsigned long trapno;
	unsigned long oldmask;
	unsigned long cr2;

	/*
	 * fpstate is really (struct _fpstate *) or (struct _xstate *)
	 * depending on the FP_XSTATE_MAGIC1 encoded in the SW reserved
	 * bytes of (struct _fpstate) and FP_XSTATE_MAGIC2 present at the end
	 * of extended memory layout. See comments at the definition of
	 * (struct _fpx_sw_bytes)
	 */
	void  *fpstate;		/* zero when no FPU/extended context */
	unsigned long reserved1[8];
};
 



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


#endif
struct _xsave_hdr {
	__u64 xstate_bv;
	__u64 reserved1[2];
	__u64 reserved2[5];
};

struct _ymmh_state {
	/* 16 * 16 bytes for each YMMH-reg */
	__u32 ymmh_space[64];
};

/*
 * Extended state pointed by the fpstate pointer in the sigcontext.
 * In addition to the fpstate, information encoded in the xstate_hdr
 * indicates the presence of other extended state information
 * supported by the processor and OS.
 */
struct _xstate {
	struct _fpstate fpstate;
	struct _xsave_hdr xstate_hdr;
	struct _ymmh_state ymmh;
	/* new processor state extensions go here */
};


#line 16 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/current.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/current.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/percpu.h" 1



#if definedEx(CONFIG_X86_64)


#endif
#if !(definedEx(CONFIG_X86_64))


#endif


















 





 

#if 1
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 1



/*
 * 'kernel.h' contains some often-used function prototypes etc
 */


#line 1 "/app/home/pgiarrusso/TypeChef/systems/redhat/usr/lib/gcc/x86_64-redhat-linux/4.4.4/include/stdarg.h" 1
/* Copyright (C) 1989, 1997, 1998, 1999, 2000, 2009 Free Software Foundation, Inc.

This file is part of GCC.

GCC is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3, or (at your option)
any later version.

GCC is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

Under Section 7 of GPL version 3, you are granted additional
permissions described in the GCC Runtime Library Exception, version
3.1, as published by the Free Software Foundation.

You should have received a copy of the GNU General Public License and
a copy of the GCC Runtime Library Exception along with this program;
see the files COPYING3 and COPYING.RUNTIME respectively.  If not, see
<http://www.gnu.org/licenses/>.  */

/*
 * ISO C Standard:  7.15  Variable arguments  <stdarg.h>
 */

#if !((!(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))
#if !((!(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))
#if !((definedEx(__need___va_list) && !((!(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))))


#endif


/* Define __gnuc_va_list.  */

#if !((!(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))

typedef __builtin_va_list __gnuc_va_list;
#endif
/* Define the standard macros for the user,
   if this invocation was from the user program.  */
#if ((!(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))) || (!((definedEx(__need___va_list) && !((!(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64)))))) && !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)))) && !((!(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))))








/* Define va_list, if desired, from __gnuc_va_list. */
/* We deliberately do not define va_list when called from
   stdio.h, because ANSI C says that stdio.h is not supposed to define
   va_list.  stdio.h needs to have access to that data type, 
   but must not use that name.  It should use the name __gnuc_va_list,
   which is safe because it is reserved for the implementation.  */
























#if 1
/* The macro _VA_LIST_ is the same thing used by this file in Ultrix.
   But on BSD NET2 we must not test or define or undef it.
   (Note that the comments in NET 2's ansi.h
   are incorrect for _VA_LIST_--see stdio.h!)  */
#if !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))
/* The macro _VA_LIST_DEFINED is used in Windows NT 3.5  */
#if !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))
/* The macro _VA_LIST is used in SCO Unix 3.2.  */
#if !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))
/* The macro _VA_LIST_T_H is used in the Bull dpx2  */
#if !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))
/* The macro __va_list__ is used by BeOS.  */
#if !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))
typedef __gnuc_va_list va_list;
#endif
#endif
#endif
#endif



#if !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))

#endif
#if !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))

#endif
#if !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))

#endif
#if !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_USE_3DNOW)) && !(definedEx(CONFIG_X86_64))))

#endif
#endif
#endif
#endif
#endif
#endif
#line 12 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/linkage.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/linkage.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/linkage.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/stringify.h" 1












#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/linkage.h" 2






/*
 * For 32-bit UML - mark functions implemented in assembly that use
 * regparm input parameters:
 */


/*
 * Make sure the compiler doesn't do anything stupid with the
 * arguments on the stack - they are owned by the *caller*, not
 * the callee. This just fools gcc into not spilling into them,
 * and keeps it from doing tailcall recursion and/or using the
 * stack slots for temporaries, since they are live and "used"
 * all the way to the end of the function.
 *
 * NOTE! On x86-64, all the arguments are in registers, so this
 * only matters on a 32-bit kernel.
 */


































#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/linkage.h" 2



#if 1

#endif









/*
 * For assembly routines.
 *
 * Note when using these that you must specify the appropriate
 * alignment directives yourself
 */



/*
 * This is used by architectures to keep arguments on the stack
 * untouched by the compiler by keeping them live until the end.
 * The argument stack may be owned by the assembly-language
 * caller, not the callee, and gcc doesn't always understand
 * that.
 *
 * We have the return value, and a maximum of six arguments.
 *
 * This should always be followed by a "return ret" for the
 * protection to work (ie no more work that the compiler might
 * end up needing stack temporaries for).
 */
/* Assembly files may be compiled with -traditional .. */













































#line 13 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/stddef.h" 1








 



	
	





 



#line 14 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 15 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 16 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/bitops.h" 1

























	
	
	
	




	
	
	
	
		
	




	









	









	









	









	









	









	




	
		
	













	
		
 

	












				    









					 










				   









				   









					
					




#line 17 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/log2.h" 1
/* Integer base 2 logarithm calculation
 *
 * Copyright (C) 2006 Red Hat, Inc. All Rights Reserved.
 * Written by David Howells (dhowells@redhat.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or (at your option) any later version.
 */




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 17 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/log2.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/bitops.h" 1

























	
	
	
	




	
	
	
	
		
	




	









	









	









	









	









	









	




	
		
	













	
		
 

	












				    









					 










				   









				   









					
					




#line 18 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/log2.h" 2

/*
 * deal with unrepresentable constant logarithms
 */
extern __attribute__((const, noreturn))
int ____ilog2_NaN(void);

/*
 * non-constant log of base 2 calculators
 * - the arch may override these in asm/bitops.h if they can be implemented
 *   more efficiently than using fls() and fls64()
 * - the arch is not required to handle n==0 if implementing the fallback
 */
#if !(definedEx(CONFIG_ARCH_HAS_ILOG2_U32))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((const))
int __ilog2_u32(u32 n)
{
	return fls(n) - 1;
}
#endif
#if !(definedEx(CONFIG_ARCH_HAS_ILOG2_U64))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((const))
int __ilog2_u64(u64 n)
{
	return fls64(n) - 1;
}
#endif
/*
 *  Determine whether some value is a power of two, where zero is
 * *not* considered a power of two.
 */

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((const))
bool is_power_of_2(unsigned long n)
{
	return (n != 0 && ((n & (n - 1)) == 0));
}

/*
 * round up to nearest power of two
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((const))
unsigned long __roundup_pow_of_two(unsigned long n)
{
	return 1UL << fls_long(n - 1);
}

/*
 * round down to nearest power of two
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((const))
unsigned long __rounddown_pow_of_two(unsigned long n)
{
	return 1UL << (fls_long(n) - 1);
}

/**
 * ilog2 - log of base 2 of 32-bit or a 64-bit unsigned value
 * @n - parameter
 *
 * constant-capable log of base 2 calculation
 * - this can be used to initialise global variables from constant data, hence
 *   the massive ternary operator construction
 *
 * selects the appropriately-sized optimised version depending on sizeof(n)
 */











































































/**
 * roundup_pow_of_two - round the given value up to nearest power of two
 * @n - parameter
 *
 * round the given value up to the nearest power of two
 * - the result is undefined when n == 0
 * - this can be used to initialise global variables from constant data
 */









/**
 * rounddown_pow_of_two - round the given value down to nearest power of two
 * @n - parameter
 *
 * round the given value down to the nearest power of two
 * - the result is undefined when n == 0
 * - this can be used to initialise global variables from constant data
 */








/**
 * order_base_2 - calculate the (rounded up) base 2 order of the argument
 * @n: parameter
 *
 * The first few values calculated by this routine:
 *  ob2(0) = 0
 *  ob2(1) = 0
 *  ob2(2) = 1
 *  ob2(3) = 2
 *  ob2(4) = 2
 *  ob2(5) = 3
 *  ... and so on.
 */




#line 18 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/typecheck.h" 1
























#line 19 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/dynamic_debug.h" 1



/* dynamic_printk_enabled, and dynamic_printk_enabled2 are bitmasks in which
 * bit n is set to 1 if any modname hashes into the bucket n, 0 otherwise. They
 * use independent hash functions, to reduce the chance of false positives.
 */
extern long long dynamic_debug_enabled;
extern long long dynamic_debug_enabled2;

/*
 * An instance of this structure is created in a special
 * ELF section at every dynamic debug callsite.  At runtime,
 * the special section is treated as an array of these.
 */
struct _ddebug {
	/*
	 * These fields are used to drive the user interface
	 * for selecting and displaying debug callsites.
	 */
	const char *modname;
	const char *function;
	const char *filename;
	const char *format;
	char primary_hash;
	char secondary_hash;
	unsigned int lineno:24;
	/*
 	 * The flags field controls the behaviour at the callsite.
 	 * The bits here are changed dynamically when the user
 	 * writes commands to <debugfs>/dynamic_debug/ddebug
	 */


	unsigned int flags:8;
} __attribute__((aligned(8)));


int ddebug_add_module(struct _ddebug *tab, unsigned int n,
				const char *modname);

#if definedEx(CONFIG_DYNAMIC_DEBUG)
extern int ddebug_remove_module(char *mod_name);






























#endif
#if !(definedEx(CONFIG_DYNAMIC_DEBUG))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int ddebug_remove_module(char *mod)
{
	return 0;
}





#endif

#line 20 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/byteorder.h" 1






#line 21 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bug.h" 1









 














 






#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bug.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/bug.h" 2




struct bug_entry {
#if !(definedEx(CONFIG_GENERIC_BUG_RELATIVE_POINTERS))
	unsigned long	bug_addr;
#endif
#if definedEx(CONFIG_GENERIC_BUG_RELATIVE_POINTERS)
	signed int	bug_addr_disp;
#endif

#if !(definedEx(CONFIG_GENERIC_BUG_RELATIVE_POINTERS))
	const char	*file;
#endif
#if definedEx(CONFIG_GENERIC_BUG_RELATIVE_POINTERS)
	signed int	file_disp;
#endif
	unsigned short	line;

	unsigned short	flags;
};



/*
 * Don't use BUG() or BUG_ON() unless there's really no way out; one
 * example might be detecting data structure corruption in the middle
 * of an operation that can't be backed out of.  If the (sub)system
 * can somehow continue operating, perhaps with reduced functionality,
 * it's probably not BUG-worthy.
 *
 * If you're tempted to BUG(), think again:  is completely giving up
 * really the *only* solution?  There are usually better options, where
 * users don't need to reboot ASAP and can mostly shut down cleanly.
 */









/*
 * WARN(), WARN_ON(), WARN_ON_ONCE, and so on can be used to report
 * significant issues that need prompt attention if they should ever
 * appear at runtime.  Use the versions with printk format strings
 * to provide better diagnostics.
 */


extern void warn_slowpath_fmt(const char *file, const int line,
		const char *fmt, ...) __attribute__((format(printf, 3, 4)));
extern void warn_slowpath_null(const char *file, const int line);




 

















 









































#if definedEx(CONFIG_SMP)

#endif
#if !(definedEx(CONFIG_SMP))

#endif

#line 40 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bug.h" 2

#line 22 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 2

extern const char linux_banner[];
extern const char linux_proc_banner[];




































#if definedEx(CONFIG_LBDAF)
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/div64.h" 1




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/div64.h" 2

/*
 * do_div() is NOT a C function. It wants to return
 * two values (the quotient and the remainder), but
 * since that doesn't work very well in C, what it
 * does is:
 *
 * - modifies the 64-bit dividend _in_place_
 * - returns the 32-bit remainder
 *
 * This ends up being the most efficient "calling
 * convention" on x86.
 */
















static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 u64 div_u64_rem(u64 dividend, u32 divisor, u32 *remainder)
{
	union {
		u64 v64;
		u32 v32[2];
	} d = { dividend };
	u32 upper;

	upper = d.v32[1];
	d.v32[1] = 0;
	if (upper >= divisor) {
		d.v32[1] = upper / divisor;
		upper %= divisor;
	}
	asm ("divl %2" : "=a" (d.v32[0]), "=d" (*remainder) :
		"rm" (divisor), "0" (d.v32[0]), "1" (upper));
	return d.v64;
}


 


#line 63 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 2

#endif
#if !(definedEx(CONFIG_LBDAF))








#endif
/**
 * upper_32_bits - return bits 32-63 of a number
 * @n: the number we're accessing
 *
 * A basic shift-right of a 64- or 32-bit quantity.  Use this to suppress
 * the "right shift count >= width of type" warning when that quantity is
 * 32-bits.
 */


/**
 * lower_32_bits - return bits 0-31 of a number
 * @n: the number we're accessing
 */











/* Use the default kernel loglevel */

/*
 * Annotation for a "continued" line of log printout (only done after a
 * line that had no enclosing \n). Only to be used by core/arch code
 * during early bootup (a continued line is not SMP-safe otherwise).
 */


extern int console_printk[];






struct completion;
struct pt_regs;
struct user;

#if definedEx(CONFIG_PREEMPT_VOLUNTARY)
extern int _cond_resched(void);

#endif
#if !(definedEx(CONFIG_PREEMPT_VOLUNTARY))

#endif
#if definedEx(CONFIG_DEBUG_SPINLOCK_SLEEP)
  void __might_sleep(char *file, int line, int preempt_offset);
/**
 * might_sleep - annotation for functions that can sleep
 *
 * this macro will print a stack trace if it is executed in an atomic
 * context (spinlock, irq-handler, ...).
 *
 * This is a useful debugging help to be able to catch problems early and not
 * be bitten later when the calling function happens to sleep when it is not
 * supposed to.
 */


#endif
#if !(definedEx(CONFIG_DEBUG_SPINLOCK_SLEEP))
  static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __might_sleep(char *file, int line, int preempt_offset) { }

#endif







#if definedEx(CONFIG_PROVE_LOCKING)
void might_fault(void);
#endif
#if !(definedEx(CONFIG_PROVE_LOCKING))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void might_fault(void)
{
	
#if (definedEx(CONFIG_DEBUG_SPINLOCK_SLEEP) && definedEx(CONFIG_DEBUG_SPINLOCK_SLEEP))
do { __might_sleep("/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/percpu.h", 138, 0); 
#if (definedEx(CONFIG_PREEMPT_VOLUNTARY) && definedEx(CONFIG_PREEMPT_VOLUNTARY))
_cond_resched()
#endif
#if (!(definedEx(CONFIG_PREEMPT_VOLUNTARY)) && !((definedEx(CONFIG_PREEMPT_VOLUNTARY) && definedEx(CONFIG_PREEMPT_VOLUNTARY))))
do { } while (0)
#endif
; } while (0)
#endif
#if (!(definedEx(CONFIG_DEBUG_SPINLOCK_SLEEP)) && !((definedEx(CONFIG_DEBUG_SPINLOCK_SLEEP) && definedEx(CONFIG_DEBUG_SPINLOCK_SLEEP))))
do { 
#if (definedEx(CONFIG_PREEMPT_VOLUNTARY) && definedEx(CONFIG_PREEMPT_VOLUNTARY))
_cond_resched()
#endif
#if (!(definedEx(CONFIG_PREEMPT_VOLUNTARY)) && !((definedEx(CONFIG_PREEMPT_VOLUNTARY) && definedEx(CONFIG_PREEMPT_VOLUNTARY))))
do { } while (0)
#endif
; } while (0)
#endif
;
}
#endif
extern struct atomic_notifier_head panic_notifier_list;
extern long (*panic_blink)(long time);
 void panic(const char * fmt, ...)
	__attribute__ ((noreturn, format (printf, 1, 2))) ;
extern void oops_enter(void);
extern void oops_exit(void);
extern int oops_may_print(void);
 void do_exit(long error_code)
	__attribute__((noreturn));
 void complete_and_exit(struct completion *, long)
	__attribute__((noreturn));
extern unsigned long simple_strtoul(const char *,char **,unsigned int);
extern long simple_strtol(const char *,char **,unsigned int);
extern unsigned long long simple_strtoull(const char *,char **,unsigned int);
extern long long simple_strtoll(const char *,char **,unsigned int);
extern int strict_strtoul(const char *, unsigned int, unsigned long *);
extern int strict_strtol(const char *, unsigned int, long *);
extern int strict_strtoull(const char *, unsigned int, unsigned long long *);
extern int strict_strtoll(const char *, unsigned int, long long *);
extern int sprintf(char * buf, const char * fmt, ...)
	__attribute__ ((format (printf, 2, 3)));
extern int vsprintf(char *buf, const char *, va_list)
	__attribute__ ((format (printf, 2, 0)));
extern int snprintf(char * buf, size_t size, const char * fmt, ...)
	__attribute__ ((format (printf, 3, 4)));
extern int vsnprintf(char *buf, size_t size, const char *fmt, va_list args)
	__attribute__ ((format (printf, 3, 0)));
extern int scnprintf(char * buf, size_t size, const char * fmt, ...)
	__attribute__ ((format (printf, 3, 4)));
extern int vscnprintf(char *buf, size_t size, const char *fmt, va_list args)
	__attribute__ ((format (printf, 3, 0)));
extern char *kasprintf(gfp_t gfp, const char *fmt, ...)
	__attribute__ ((format (printf, 2, 3)));
extern char *kvasprintf(gfp_t gfp, const char *fmt, va_list args);

extern int sscanf(const char *, const char *, ...)
	__attribute__ ((format (scanf, 2, 3)));
extern int vsscanf(const char *, const char *, va_list)
	__attribute__ ((format (scanf, 2, 0)));

extern int get_option(char **str, int *pint);
extern char *get_options(const char *str, int nints, int *ints);
extern unsigned long long memparse(const char *ptr, char **retptr);

extern int core_kernel_text(unsigned long addr);
extern int __kernel_text_address(unsigned long addr);
extern int kernel_text_address(unsigned long addr);
extern int func_ptr_is_kernel_text(void *ptr);

struct pid;
extern struct pid *session_of_pgrp(struct pid *pgrp);

/*
 * FW_BUG
 * Add this to a message where you are sure the firmware is buggy or behaves
 * really stupid or out of spec. Be aware that the responsible BIOS developer
 * should be able to fix this issue or at least get a concrete idea of the
 * problem by reading your message without the need of looking at the kernel
 * code.
 * 
 * Use it for definite and high priority BIOS bugs.
 *
 * FW_WARN
 * Use it for not that clear (e.g. could the kernel messed up things already?)
 * and medium priority BIOS bugs.
 *
 * FW_INFO
 * Use this one if you want to tell the user or vendor about something
 * suspicious, but generally harmless related to the firmware.
 *
 * Use it for information or very low priority BIOS bugs.
 */





 __attribute__((regparm(0))) int vprintk(const char *fmt, va_list args)
	__attribute__ ((format (printf, 1, 0)));
 __attribute__((regparm(0))) int printk(const char * fmt, ...)
	__attribute__ ((format (printf, 1, 2))) ;

extern int __printk_ratelimit(const char *func);

extern bool printk_timed_ratelimit(unsigned long *caller_jiffies,
				   unsigned int interval_msec);

extern int printk_delay_msec;

/*
 * Print a one-time message (analogous to WARN_ONCE() et al):
 */









void log_buf_kexec_setup(void);
 
	


	













extern int printk_needs_cpu(int cpu);
extern void printk_tick(void);

extern void  __attribute__((regparm(0))) __attribute__((format(printf, 1, 2)))
	early_printk(const char *fmt, ...);

unsigned long int_sqrt(unsigned long);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void console_silent(void)
{
	(console_printk[0]) = 0;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void console_verbose(void)
{
	if ((console_printk[0]))
		(console_printk[0]) = 15;
}

extern void bust_spinlocks(int yes);
extern void wake_up_klogd(void);
extern int oops_in_progress;		/* If set, an oops, panic(), BUG() or die() is in progress */
extern int panic_timeout;
extern int panic_on_oops;
extern int panic_on_unrecovered_nmi;
extern int panic_on_io_nmi;
extern const char *print_tainted(void);
extern void add_taint(unsigned flag);
extern int test_taint(unsigned flag);
extern unsigned long get_taint(void);
extern int root_mountflags;

/* Values used for system_state */
extern enum system_states {
	SYSTEM_BOOTING,
	SYSTEM_RUNNING,
	SYSTEM_HALT,
	SYSTEM_POWER_OFF,
	SYSTEM_RESTART,
	SYSTEM_SUSPEND_DISK,
} system_state;













extern void dump_stack(void) ;

enum {
	DUMP_PREFIX_NONE,
	DUMP_PREFIX_ADDRESS,
	DUMP_PREFIX_OFFSET
};
extern void hex_dump_to_buffer(const void *buf, size_t len,
				int rowsize, int groupsize,
				char *linebuf, size_t linebuflen, bool ascii);
extern void print_hex_dump(const char *level, const char *prefix_str,
				int prefix_type, int rowsize, int groupsize,
				const void *buf, size_t len, bool ascii);
extern void print_hex_dump_bytes(const char *prefix_str, int prefix_type,
			const void *buf, size_t len);

extern const char hex_asc[];



static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 char *pack_hex_byte(char *buf, u8 byte)
{
	*buf++ = hex_asc[((byte) & 0xf0) >> 4];
	*buf++ = hex_asc[((byte) & 0x0f)];
	return buf;
}





















/* pr_devel() should produce zero code unless DEBUG is defined */



#if 1


#endif
/* If you are writing a driver, please use dev_dbg instead */



#if definedEx(CONFIG_DYNAMIC_DEBUG)
/* dynamic_pr_debug() uses pr_fmt() internally so we don't need it here */


#endif
#if !(definedEx(CONFIG_DYNAMIC_DEBUG))


#endif
/*
 * ratelimited messages with local ratelimit_state,
 * no local ratelimit_state used in the !PRINTK case
 */










 
















/* no pr_cont_ratelimited, don't do that... */
/* If you are writing a driver, please use dev_dbg instead */



#if 1



#endif
/*
 * General tracing related utility functions - trace_printk(),
 * tracing_on/tracing_off and tracing_start()/tracing_stop
 *
 * Use tracing_on/tracing_off when you want to quickly turn on or off
 * tracing. It simply enables or disables the recording of the trace events.
 * This also corresponds to the user space /sys/kernel/debug/tracing/tracing_on
 * file, which gives a means for the kernel and userspace to interact.
 * Place a tracing_off() in the kernel where you want tracing to end.
 * From user space, examine the trace, and then echo 1 > tracing_on
 * to continue tracing.
 *
 * tracing_stop/tracing_start has slightly more overhead. It is used
 * by things like suspend to ram where disabling the recording of the
 * trace is not enough, but tracing must actually stop because things
 * like calling smp_processor_id() may crash the system.
 *
 * Most likely, you want to use tracing_on/tracing_off.
 */
#if definedEx(CONFIG_RING_BUFFER)
void tracing_on(void);
void tracing_off(void);
/* trace_off_permanent stops recording with no way to bring it back */
void tracing_off_permanent(void);
int tracing_is_on(void);
#endif
#if !(definedEx(CONFIG_RING_BUFFER))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void tracing_on(void) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void tracing_off(void) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void tracing_off_permanent(void) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int tracing_is_on(void) { return 0; }
#endif
#if definedEx(CONFIG_TRACING)
extern void tracing_start(void);
extern void tracing_stop(void);
extern void ftrace_off_permanent(void);

extern void
ftrace_special(unsigned long arg1, unsigned long arg2, unsigned long arg3);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __attribute__ ((format (printf, 1, 2)))
____trace_printk_check_format(const char *fmt, ...)
{
}






/**
 * trace_printk - printf formatting in the ftrace buffer
 * @fmt: the printf format for printing
 *
 * Note: __trace_printk is an internal function for trace_printk and
 *       the @ip is passed in via the trace_printk macro.
 *
 * This function allows a kernel developer to debug fast path sections
 * that printk is not appropriate for. By scattering in various
 * printk like tracing in the code, a developer can quickly see
 * where problems are occurring.
 *
 * This is intended as a debugging tool for the developer only.
 * Please refrain from leaving trace_printks scattered around in
 * your code.
 */














extern int
__trace_bprintk(unsigned long ip, const char *fmt, ...)
	__attribute__ ((format (printf, 2, 3)));

extern int
__trace_printk(unsigned long ip, const char *fmt, ...)
	__attribute__ ((format (printf, 2, 3)));

extern void trace_dump_stack(void);

/*
 * The double __builtin_constant_p is because gcc will give us an error
 * if we try to allocate the static variable to fmt if it is not a
 * constant. Even with the outer if statement.
 */












extern int
__ftrace_vbprintk(unsigned long ip, const char *fmt, va_list ap);

extern int
__ftrace_vprintk(unsigned long ip, const char *fmt, va_list ap);

extern void ftrace_dump(void);
#endif
#if !(definedEx(CONFIG_TRACING))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void
ftrace_special(unsigned long arg1, unsigned long arg2, unsigned long arg3) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int
trace_printk(const char *fmt, ...) __attribute__ ((format (printf, 1, 2)));

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void tracing_start(void) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void tracing_stop(void) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void ftrace_off_permanent(void) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void trace_dump_stack(void) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int
trace_printk(const char *fmt, ...)
{
	return 0;
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int
ftrace_vprintk(const char *fmt, va_list ap)
{
	return 0;
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void ftrace_dump(void) { }
#endif
/*
 *      Display an IP address in readable format.
 */








/*
 * min()/max()/clamp() macros that also do
 * strict type-checking.. See the
 * "unnecessary" pointer comparison.
 */












/**
 * clamp - return a value clamped to a given range with strict typechecking
 * @val: current value
 * @min: minimum allowable value
 * @max: maximum allowable value
 *
 * This macro does strict typechecking of min/max to make sure they are of the
 * same type as val.  See the unnecessary pointer comparisons.
 */









/*
 * ..and if you can't take the strict
 * types, you can specify one yourself.
 *
 * Or not use min/max/clamp at all, of course.
 */










/**
 * clamp_t - return a value clamped to a given range using a given type
 * @type: the type of variable to use
 * @val: current value
 * @min: minimum allowable value
 * @max: maximum allowable value
 *
 * This macro does no typechecking and uses temporary variables of type
 * 'type' to make all the comparisons.
 */







/**
 * clamp_val - return a value clamped to a given range using val's type
 * @val: current value
 * @min: minimum allowable value
 * @max: maximum allowable value
 *
 * This macro does no typechecking and uses temporary variables of whatever
 * type the input argument 'val' is.  This is useful when val is an unsigned
 * type and min and max are literals that will otherwise be assigned a signed
 * integer type.
 */








/*
 * swap - swap value of @a and @b
 */



/**
 * container_of - cast a member of a structure out to the containing structure
 * @ptr:	the pointer to the member.
 * @type:	the type of the container struct this is embedded in.
 * @member:	the name of the member within the struct.
 *
 */




struct sysinfo;
extern int do_sysinfo(struct sysinfo *info);








struct sysinfo {
	long uptime;			/* Seconds since boot */
	unsigned long loads[3];		/* 1, 5, and 15 minute load averages */
	unsigned long totalram;		/* Total usable main memory size */
	unsigned long freeram;		/* Available memory size */
	unsigned long sharedram;	/* Amount of shared memory */
	unsigned long bufferram;	/* Memory used by buffers */
	unsigned long totalswap;	/* Total swap space size */
	unsigned long freeswap;		/* swap space still available */
	unsigned short procs;		/* Number of current processes */
	unsigned short pad;		/* explicit padding for m68k */
	unsigned long totalhigh;	/* Total high memory size */
	unsigned long freehigh;		/* Available high memory size */
	unsigned int mem_unit;		/* Memory unit size in bytes */
	char _f[20-2*sizeof(long)-sizeof(int)];	/* Padding: libc5 uses this.. */
};

/* Force a compilation error if condition is true */


/* Force a compilation error if condition is constant and true */


/* Force a compilation error if a constant expression is not a power of 2 */



/* Force a compilation error if condition is true, but also produce a
   result (of value 0 and type size_t), so the expression can be used
   e.g. in a structure initializer (or where-ever else comma expressions
   aren't permitted). */



/* Trap pasters of __FUNCTION__ at compile-time */


/* This helps us to avoid #ifdef CONFIG_NUMA */
#if definedEx(CONFIG_NUMA)

#endif
#if !(definedEx(CONFIG_NUMA))

#endif
/* Rebuild everything on CONFIG_FTRACE_MCOUNT_RECORD */
#if definedEx(CONFIG_FTRACE_MCOUNT_RECORD)

#endif

#line 47 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/percpu.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/stringify.h" 1












#line 48 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/percpu.h" 2

#if definedEx(CONFIG_SMP)


#endif
#if !(definedEx(CONFIG_SMP))

#endif
/*
 * Initialized pointers to per-cpu variables needed for the boot
 * processor need to use these macros to get the proper address
 * offset from __per_cpu_load on SMP.
 *
 * There also must be an entry in vmlinux_64.lds.S
 */



#if definedEx(CONFIG_X86_64_SMP)

#endif
#if !(definedEx(CONFIG_X86_64_SMP))

#endif
/* For arch-specific code, we can use direct single-insn ops (they
 * don't give an lvalue though). */
extern void __bad_percpu_size(void);






























































/*
 * percpu_read() makes gcc load the percpu variable every time it is
 * accessed while percpu_read_stable() allows the value to be cached.
 * percpu_read_stable() is more efficient and can be used if its value
 * is guaranteed to be valid across cpus.  The current users include
 * get_current() and get_thread_info() both of which are actually
 * per-thread variables implemented as per-cpu variables and thus
 * stable for the duration of the respective task.
 */































































/*
 * Per cpu atomic 64 bit operations are only available under 64 bit.
 * 32 bit must fall back to generic operations.
 */
#if definedEx(CONFIG_X86_64)



















#endif
/* This is not atomic against other CPUs -- CPU preemption needs to be off */









#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/percpu.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/percpu.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/threads.h" 1




/*
 * The default limit for the nr of threads is now in
 * /proc/sys/kernel/threads-max.
 */

/*
 * Maximum supported processors.  Setting this smaller saves quite a
 * bit of memory.  Use nr_cpu_ids instead of this except for static bitmaps.
 */




/* Places which use this should consider cpumask_var_t. */




/*
 * This controls the default maximum pid allocated to a process
 */


/*
 * A maximum of 4 million PIDs should be enough for a while.
 * [NOTE: PID/TIDs are limited to 2^29 ~= 500+ million, see futex.h.]
 */




#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/percpu.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/percpu-defs.h" 1



/*
 * Determine the real variable name from the name visible in the
 * kernel sources.
 */


/*
 * Base implementations of per-CPU variable declarations and definitions, where
 * the section in which the variable is to be placed is provided by the
 * 'sec' argument.  This may be used to affect the parameters governing the
 * variable's storage.
 *
 * NOTE!  The sections for the DECLARE and for the DEFINE must match, lest
 * linkage errors occur due the compiler generating the wrong code to access
 * that section.
 */







/*
 * s390 and alpha modules require percpu variables to be defined as
 * weak to force the compiler to generate GOT based external
 * references for them.  This is necessary because percpu sections
 * will be located outside of the usually addressable area.
 *
 * This definition puts the following two extra restrictions when
 * defining percpu variables.
 *
 * 1. The symbol must be globally unique, even the static ones.
 * 2. Static percpu variables cannot be defined inside a function.
 *
 * Archs which need weak percpu definitions should define
 * ARCH_NEEDS_WEAK_PER_CPU in asm/percpu.h when necessary.
 *
 * To ensure that the generic code observes the above two
 * restrictions, if CONFIG_DEBUG_FORCE_WEAK_PER_CPU is set weak
 * definition is used for all cases.
 */
#if definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)
/*
 * __pcpu_scope_* dummy variable is used to enforce scope.  It
 * receives the static modifier when it's used in front of
 * DEFINE_PER_CPU() and will trigger build failure if
 * DECLARE_PER_CPU() is used for the same variable.
 *
 * __pcpu_unique_* dummy variable is used to enforce symbol uniqueness
 * such that hidden weak symbol collision, which will cause unrelated
 * variables to share the same address, can be detected during build.
 */










#endif
#if !(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
/*
 * Normal declaration and definition macros.
 */






#endif
/*
 * Variant on the per-CPU variable declaration/definition theme used for
 * ordinary per-CPU variables.
 */






/*
 * Declaration/definition used for per-CPU variables that must come first in
 * the set of variables.
 */






/*
 * Declaration/definition used for per-CPU variables that must be cacheline
 * aligned under SMP conditions so that, whilst a particular instance of the
 * data corresponds to a particular CPU, inefficiencies due to direct access by
 * other CPUs are reduced by preventing the data from unnecessarily spanning
 * cachelines.
 *
 * An example of this would be statistical data, where each CPU's set of data
 * is updated by that CPU alone, but the data from across all CPUs is collated
 * by a CPU processing a read from a proc file.
 */
















/*
 * Declaration/definition used for per-CPU variables that must be page aligned.
 */








/*
 * Intermodule exports for per-CPU variables.
 */





#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/percpu.h" 2

#if definedEx(CONFIG_SMP)
/*
 * per_cpu_offset() is the offset that has to be added to a
 * percpu variable to get to the instance for a certain processor.
 *
 * Most arches use the __per_cpu_offset array for those offsets but
 * some arches have their own ways of determining the offset (x86_64, s390).
 */

extern unsigned long __per_cpu_offset[1];



/*
 * Determine the offset for the currently active processor.
 * An arch may define __my_cpu_offset to provide a more effective
 * means of obtaining the offset to the per cpu variables of the
 * current processor.
 */



#if definedEx(CONFIG_DEBUG_PREEMPT)

#endif
#if !(definedEx(CONFIG_DEBUG_PREEMPT))

#endif
/*
 * Add a offset to a pointer but keep the pointer as is.
 *
 * Only S390 provides its own means of moving the pointer.
 */



/*
 * A percpu variable may point to a discarded regions. The following are
 * established ways to produce a usable pointer from the percpu variable
 * offset.
 */












extern void setup_per_cpu_areas(void);

#endif
#if !(definedEx(CONFIG_SMP))






#endif

#if definedEx(CONFIG_SMP)

#endif
#if !(definedEx(CONFIG_SMP))

#endif

#if definedEx(CONFIG_SMP)



#if 1


#endif


#endif
#if !(definedEx(CONFIG_SMP))




#endif







#line 246 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/percpu.h" 2

/* We can use this directly for local CPU (faster). */

#if (definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_this_cpu_off; extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(unsigned long) per_cpu__this_cpu_off
#endif
#if (!(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)) && !((definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))))
extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(unsigned long) per_cpu__this_cpu_off
#endif
;

#endif
#if definedEx(CONFIG_SMP)
/*
 * Define the "EARLY_PER_CPU" macros.  These are used for some per_cpu
 * variables that are initialized and accessed before there are per_cpu
 * areas allocated.
 */






















#endif
#if !(definedEx(CONFIG_SMP))











/* no early_per_cpu_map() */

#endif

#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/current.h" 2


struct task_struct;


#if (definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_current_task; extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(struct task_struct *) per_cpu__current_task
#endif
#if (!(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)) && !((definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))))
extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(struct task_struct *) per_cpu__current_task
#endif
;

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline)) struct task_struct *get_current(void)
{
	return ({ typeof(per_cpu__current_task) pfo_ret__; switch (sizeof(per_cpu__current_task)) { case 1: asm("mov" "b "
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
"%%""#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))\ngs\n#elif !(definedEx(CONFIG_X86_64))\nfs\n#endif\n"":%P" "1"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
"%P" "1"
#endif
",%0" : "=q" (pfo_ret__) : "p"(&per_cpu__current_task)); break; case 2: asm("mov" "w "
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
"%%""#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))\ngs\n#elif !(definedEx(CONFIG_X86_64))\nfs\n#endif\n"":%P" "1"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
"%P" "1"
#endif
",%0" : "=r" (pfo_ret__) : "p"(&per_cpu__current_task)); break; case 4: asm("mov" "l "
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
"%%""#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))\ngs\n#elif !(definedEx(CONFIG_X86_64))\nfs\n#endif\n"":%P" "1"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
"%P" "1"
#endif
",%0" : "=r" (pfo_ret__) : "p"(&per_cpu__current_task)); break; case 8: asm("mov" "q "
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
"%%""#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))\ngs\n#elif !(definedEx(CONFIG_X86_64))\nfs\n#endif\n"":%P" "1"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
"%P" "1"
#endif
",%0" : "=r" (pfo_ret__) : "p"(&per_cpu__current_task)); break; default: __bad_percpu_size(); } pfo_ret__; });
}





#line 17 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/cpufeature.h" 1
/*
 * Defines x86 CPU feature bits
 */





























					  





























































































































































































































 























#line 18 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/system.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/asm.h" 1






 




 






























 






#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/system.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/segment.h" 1





























































































 




 









































 



































































#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/system.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/cpufeature.h" 1
/*
 * Defines x86 CPU feature bits
 */





























					  





























































































































































































































 























#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/system.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/cmpxchg.h" 1

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/cmpxchg_32.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/bitops.h" 1

























	
	
	
	




	
	
	
	
		
	




	









	









	









	









	









	









	




	
		
	













	
		
 

	












				    









					 










				   









				   









					
					




#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/cmpxchg_32.h" 2

/*
 * Note: if you use set64_bit(), __cmpxchg64(), or their variants, you
 *       you need to test for the feature in boot_cpu_data.
 */

extern void __xchg_wrong_size(void);

/*
 * Note: no "lock" prefix even on SMP: xchg always implies lock anyway
 * Note 2: xchg has side effect, so that attribute volatile is necessary,
 *	  but generally the primitive is invalid, *ptr is output argument. --ANK
 */

struct __xchg_dummy {
	unsigned long a[100];
};

































/*
 * The semantics of XCHGCMP8B are a bit strange, this is why
 * there is a loop and the loading of %%eax and %%edx has to
 * be inside. This inlines well in most cases, the cached
 * cost is around ~38 cycles. (in the future we might want
 * to do an SIMD/3DNOW!/MMX/FPU 64-bit store here, but that
 * might have an implicit FPU-save as a cost, so it's not
 * clear which path to go.)
 *
 * cmpxchg8b must be used with the lock prefix here to allow
 * the instruction to be executed atomically, see page 3-102
 * of the instruction set reference 24319102.pdf. We need
 * the reader side to see the coherent 64bit value.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __set_64bit(unsigned long long *ptr,
			       unsigned int low, unsigned int high)
{
	asm volatile("\n1:\t"
		     "movl (%0), %%eax\n\t"
		     "movl 4(%0), %%edx\n\t"
		     
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "cmpxchg8b (%0)\n\t"
		     "jnz 1b"
		     : /* no outputs */
		     : "D"(ptr),
		       "b"(low),
		       "c"(high)
		     : "ax", "dx", "memory");
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __set_64bit_constant(unsigned long long *ptr,
					unsigned long long value)
{
	__set_64bit(ptr, (unsigned int)value, (unsigned int)(value >> 32));
}




static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __set_64bit_var(unsigned long long *ptr,
				   unsigned long long value)
{
	__set_64bit(ptr, *(((unsigned int *)&(value)) + 0), *(((unsigned int *)&(value)) + 1));
}












extern void __cmpxchg_wrong_size(void);

/*
 * Atomic compare and exchange.  Compare OLD with MEM, if identical,
 * store NEW in MEM.  Return the initial value in MEM.  Success is
 * indicated by comparing RETURN with OLD.
 */



























































static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long long __cmpxchg64(volatile void *ptr,
					     unsigned long long old,
					     unsigned long long new)
{
	unsigned long long prev;
	asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "cmpxchg8b %3"
		     : "=A"(prev)
		     : "b"((unsigned long)new),
		       "c"((unsigned long)(new >> 32)),
		       "m"(*((struct __xchg_dummy *)(ptr))),
		       "0"(old)
		     : "memory");
	return prev;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long long __cmpxchg64_local(volatile void *ptr,
						   unsigned long long old,
						   unsigned long long new)
{
	unsigned long long prev;
	asm volatile("cmpxchg8b %3"
		     : "=A"(prev)
		     : "b"((unsigned long)new),
		       "c"((unsigned long)(new >> 32)),
		       "m"(*((struct __xchg_dummy *)(ptr))),
		       "0"(old)
		     : "memory");
	return prev;
}













					

	
	
		
	
		
	
		
	
	






































































#line 4 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/cmpxchg.h" 2
 

#line 9 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/system.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/nops.h" 1



/* Define nops for use with alternative() */

/* generic versions from gas
   1: nop
   the following instructions are NOT nops in 64-bit mode,
   for 64-bit mode use K8 or P6 nops instead
   2: movl %esi,%esi
   3: leal 0x00(%esi),%esi
   4: leal 0x00(,%esi,1),%esi
   6: leal 0x00000000(%esi),%esi
   7: leal 0x00000000(,%esi,1),%esi
*/









/* Opteron 64bit nops
   1: nop
   2: osp nop
   3: osp osp nop
   4: osp osp osp nop
*/









/* K7 nops
   uses eax dependencies (arbitary choice)
   1: nop
   2: movl %eax,%eax
   3: leal (,%eax,1),%eax
   4: leal 0x00(,%eax,1),%eax
   6: leal 0x00000000(%eax),%eax
   7: leal 0x00000000(,%eax,1),%eax
*/









/* P6 nops
   uses eax dependencies (Intel-recommended choice)
   1: nop
   2: osp nop
   3: nopl (%eax)
   4: nopl 0x00(%eax)
   5: nopl 0x00(%eax,%eax,1)
   6: osp nopl 0x00(%eax,%eax,1)
   7: nopl 0x00000000(%eax)
   8: nopl 0x00000000(%eax,%eax,1)
   Note: All the above are assumed to be a single instruction.
	There is kernel code that depends on this.
*/









#if definedEx(CONFIG_MK7)








#endif
#if (definedEx(CONFIG_X86_P6_NOP) && !(definedEx(CONFIG_MK7)))








#endif
#if (definedEx(CONFIG_X86_64) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))








#endif
#if (!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))








#endif



#line 10 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/system.h" 2

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 1





























































 
























































 


  












   











 

	





	




	

	









	

	

	

	

	

	

	



	

	







































	

	




				   
















 
	


	

















	





	




	
		

















	
	
	
	
	
	

















	
	
	


				
				

				
				

			







	
	
	


























 






 


 
















 





















 




























 






















































	



	



























 











	




	

































































































































	
	
	
	
	
	
	
	
	
	
	
	
	
	

























 






#line 12 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/system.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/irqflags.h" 1
/*
 * include/linux/irqflags.h
 *
 * IRQ flags tracing: follow the state of the hardirq and softirq flags and
 * provide callbacks for transitions between ON and OFF states.
 *
 * This file gets included from lowlevel asm headers too, to provide
 * wrapped versions of the local_irq_*() APIs, based on the
 * raw_local_irq_*() macros from the lowlevel headers.
 */



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/typecheck.h" 1
























#line 16 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/irqflags.h" 2

#if definedEx(CONFIG_TRACE_IRQFLAGS)
  extern void trace_softirqs_on(unsigned long ip);
  extern void trace_softirqs_off(unsigned long ip);
  extern void trace_hardirqs_on(void);
  extern void trace_hardirqs_off(void);









#endif
#if !(definedEx(CONFIG_TRACE_IRQFLAGS))













#endif
#if (definedEx(CONFIG_IRQSOFF_TRACER) || definedEx(CONFIG_PREEMPT_TRACER))
 extern void stop_critical_timings(void);
 extern void start_critical_timings(void);
#endif
#if !((definedEx(CONFIG_IRQSOFF_TRACER) || definedEx(CONFIG_PREEMPT_TRACER)))


#endif

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/irqflags.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor-flags.h" 1






























































































 



#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/irqflags.h" 2


/*
 * Interrupt control:
 */

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long native_save_fl(void)
{
	unsigned long flags;

	/*
	 * "=rm" is safe here, because "pop" adjusts the stack before
	 * it evaluates its effective address -- this is part of the
	 * documented behavior of the "pop" instruction.
	 */
	asm volatile("# __raw_save_flags\n\t"
		     "pushf ; pop %0"
		     : "=rm" (flags)
		     : /* no input */
		     : "memory");

	return flags;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_restore_fl(unsigned long flags)
{
	asm volatile("push %0 ; popf"
		     : /* no output */
		     :"g" (flags)
		     :"memory", "cc");
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_irq_disable(void)
{
	asm volatile("cli": : :"memory");
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_irq_enable(void)
{
	asm volatile("sti": : :"memory");
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_safe_halt(void)
{
	asm volatile("sti; hlt": : :"memory");
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_halt(void)
{
	asm volatile("hlt": : :"memory");
}




#if 1

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long __raw_local_save_flags(void)
{
	return native_save_fl();
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void raw_local_irq_restore(unsigned long flags)
{
	native_restore_fl(flags);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void raw_local_irq_disable(void)
{
	native_irq_disable();
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void raw_local_irq_enable(void)
{
	native_irq_enable();
}

/*
 * Used in the idle loop; sti takes one instruction cycle
 * to complete:
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void raw_safe_halt(void)
{
	native_safe_halt();
}

/*
 * Used when interrupts are already enabled or to
 * shutdown the processor:
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void halt(void)
{
	native_halt();
}

/*
 * For spinlocks, etc:
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long __raw_local_irq_save(void)
{
	unsigned long flags = __raw_local_save_flags();

	raw_local_irq_disable();

	return flags;
}
 





























 




#endif







static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int raw_irqs_disabled_flags(unsigned long flags)
{
	return !(flags & 0x00000200);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int raw_irqs_disabled(void)
{
	unsigned long flags = __raw_local_save_flags();

	return raw_irqs_disabled_flags(flags);
}

 










 













 





 




#line 59 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/irqflags.h" 2
























 












































#line 13 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/system.h" 2

/* entries in ARCH_DLINFO: */
#if (definedEx(CONFIG_IA32_EMULATION) || !(definedEx(CONFIG_X86_64)))

#endif
#if !((definedEx(CONFIG_IA32_EMULATION) || !(definedEx(CONFIG_X86_64))))

#endif
struct task_struct; /* one of the stranger aspects of C forward declarations */
struct task_struct *__switch_to(struct task_struct *prev,
				struct task_struct *next);
struct tss_struct;
void __switch_to_xtra(struct task_struct *prev_p, struct task_struct *next_p,
		      struct tss_struct *tss);
extern void show_regs_common(void);


#if definedEx(CONFIG_CC_STACKPROTECTOR)







#endif
#if !(definedEx(CONFIG_CC_STACKPROTECTOR))



#endif
/*
 * Saving eflags is important. It switches not only IOPL between tasks,
 * it also protects other tasks from NT leaking through sysenter etc.
 */
















































/*
 * disable hlt during certain critical i/o operations
 */

 


















 




























extern void native_load_gs_index(unsigned);

/*
 * Load a segment. Fall back on loading the zero
 * segment if something goes wrong..
 */

















/*
 * Save a segment register away
 */



/*
 * x86_32 user gs accessors.
 */







 






static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long get_limit(unsigned long segment)
{
	unsigned long __limit;
	asm("lsll %1,%0" : "=r" (__limit) : "r" (segment));
	return __limit + 1;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_clts(void)
{
	asm volatile("clts");
}

/*
 * Volatile isn't enough to prevent the compiler from reordering the
 * read/write functions for the control registers and messing everything up.
 * A memory clobber would solve the problem, but would prevent reordering of
 * all loads stores around it, which can hurt performance. Solution is to
 * use a variable and mimic reads and writes to it to enforce serialization
 */
static unsigned long __force_order;

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long native_read_cr0(void)
{
	unsigned long val;
	asm volatile("mov %%cr0,%0\n\t" : "=r" (val), "=m" (__force_order));
	return val;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_write_cr0(unsigned long val)
{
	asm volatile("mov %0,%%cr0": : "r" (val), "m" (__force_order));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long native_read_cr2(void)
{
	unsigned long val;
	asm volatile("mov %%cr2,%0\n\t" : "=r" (val), "=m" (__force_order));
	return val;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_write_cr2(unsigned long val)
{
	asm volatile("mov %0,%%cr2": : "r" (val), "m" (__force_order));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long native_read_cr3(void)
{
	unsigned long val;
	asm volatile("mov %%cr3,%0\n\t" : "=r" (val), "=m" (__force_order));
	return val;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_write_cr3(unsigned long val)
{
	asm volatile("mov %0,%%cr3": : "r" (val), "m" (__force_order));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long native_read_cr4(void)
{
	unsigned long val;
	asm volatile("mov %%cr4,%0\n\t" : "=r" (val), "=m" (__force_order));
	return val;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long native_read_cr4_safe(void)
{
	unsigned long val;
	/* This could fault if %cr4 does not exist. In x86_64, a cr4 always
	 * exists, so it will never fail. */

	asm volatile("1: mov %%cr4, %0\n"
		     "2:\n"
		     " .section __ex_table,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "1b" "," "2b" "\n" " .previous\n"
		     : "=r" (val), "=m" (__force_order) : "0" (0));
 	

	return val;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_write_cr4(unsigned long val)
{
	asm volatile("mov %0,%%cr4": : "r" (val), "m" (__force_order));
}

#if definedEx(CONFIG_X86_64)
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long native_read_cr8(void)
{
	unsigned long cr8;
	asm volatile("movq %%cr8,%0" : "=r" (cr8));
	return cr8;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_write_cr8(unsigned long val)
{
	asm volatile("movq %0,%%cr8" :: "r" (val) : "memory");
}
#endif
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_wbinvd(void)
{
	asm volatile("wbinvd": : :"memory");
}



#if 1










#if definedEx(CONFIG_X86_64)



#endif
/* Clear the 'TS' bit */


#endif



static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void clflush(volatile void *__p)
{
	asm volatile("clflush %0" : "+m" (*(volatile char  *)__p));
}



void disable_hlt(void);
void enable_hlt(void);

void cpu_idle_wait(void);

extern unsigned long arch_align_stack(unsigned long sp);
extern void free_init_pages(char *what, unsigned long begin, unsigned long end);

void default_idle(void);

void stop_this_cpu(void *dummy);

/*
 * Force strict CPU ordering.
 * And yes, this is required on UP too when we're talking
 * to devices.
 */

/*
 * Some non-Intel clones support out of order store. wmb() ceases to be a
 * nop for these.
 */



 



/**
 * read_barrier_depends - Flush all pending reads that subsequents reads
 * depend on.
 *
 * No data-dependent reads from memory-like regions are ever reordered
 * over this barrier.  All reads preceding this primitive are guaranteed
 * to access memory (but not necessarily other CPUs' caches) before any
 * reads following this primitive that depend on the data return by
 * any of the preceding reads.  This primitive is much lighter weight than
 * rmb() on most CPUs, and is never heavier weight than is
 * rmb().
 *
 * These ordering constraints are respected by both the local CPU
 * and the compiler.
 *
 * Ordering is not guaranteed by anything other than these primitives,
 * not even by data dependencies.  See the documentation for
 * memory_barrier() for examples and URLs to more information.
 *
 * For example, the following code would force ordering (the initial
 * value of "a" is zero, "b" is one, and "p" is "&a"):
 *
 * <programlisting>
 *	CPU 0				CPU 1
 *
 *	b = 2;
 *	memory_barrier();
 *	p = &b;				q = p;
 *					read_barrier_depends();
 *					d = *q;
 * </programlisting>
 *
 * because the read of "*q" depends on the read of "p" and these
 * two reads are separated by a read_barrier_depends().  However,
 * the following code, with the same initial values for "a" and "b":
 *
 * <programlisting>
 *	CPU 0				CPU 1
 *
 *	a = 2;
 *	memory_barrier();
 *	b = 3;				y = b;
 *					read_barrier_depends();
 *					x = a;
 * </programlisting>
 *
 * does not enforce ordering, since there is no data dependency between
 * the read of "a" and the read of "b".  Therefore, on some CPUs, such
 * as Alpha, "y" could be set to 3 and "x" to 0.  Use rmb()
 * in cases like this where there are no data dependencies.
 **/



#if definedEx(CONFIG_SMP)

#if definedEx(CONFIG_X86_PPRO_FENCE)

#endif
#if !(definedEx(CONFIG_X86_PPRO_FENCE))

#endif
#if definedEx(CONFIG_X86_OOSTORE)

#endif
#if !(definedEx(CONFIG_X86_OOSTORE))

#endif


#endif
#if !(definedEx(CONFIG_SMP))





#endif
/*
 * Stop RDTSC speculation. This is needed when you need to use RDTSC
 * (or get_cycles or vread that possibly accesses the TSC) in a defined
 * code region.
 *
 * (Could use an alternative three way for this if there was one.)
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void rdtsc_barrier(void)
{
	asm volatile ("661:\n\t" 
#if (definedEx(CONFIG_MK7) && !((!(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_P6_NOP))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_64))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_64)))))
".byte 0x8d,0x04,0x20\n"
#endif
#if (!((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && !((definedEx(CONFIG_X86_64) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && definedEx(CONFIG_X86_P6_NOP) && !(definedEx(CONFIG_MK7)) && !((definedEx(CONFIG_MK7) && !((!(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_P6_NOP))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_64))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_64)))))))
".byte 0x0f,0x1f,0x00\n"
#endif
#if (!((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && definedEx(CONFIG_X86_64) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)) && !((definedEx(CONFIG_MK7) && !((!(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_P6_NOP))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_64))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_64)))))) && !((!((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && !((definedEx(CONFIG_X86_64) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && definedEx(CONFIG_X86_P6_NOP) && !(definedEx(CONFIG_MK7)))))
".byte 0x66,0x66,0x90\n"
#endif
#if (!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_MK7) && !((!(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_P6_NOP))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_64))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_64)))))) && !((!((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && !((definedEx(CONFIG_X86_64) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && definedEx(CONFIG_X86_P6_NOP) && !(definedEx(CONFIG_MK7)))) && !((!((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && definedEx(CONFIG_X86_64) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))))
".byte 0x8d,0x76,0x00\n"
#endif
 "\n662:\n" ".section .altinstructions,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661b\n" " " ".long" " " "663f\n" "	 .byte " "(3*32+17)" "\n" "	 .byte 662b-661b\n" "	 .byte 664f-663f\n" "	 .byte 0xff + (664f-663f) - (662b-661b)\n" ".previous\n" ".section .altinstr_replacement, \"ax\"\n" "663:\n\t" "mfence" "\n664:\n" ".previous" : : : "memory");
	asm volatile ("661:\n\t" 
#if (definedEx(CONFIG_MK7) && !((!(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_P6_NOP))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_64))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_64)))))
".byte 0x8d,0x04,0x20\n"
#endif
#if (!((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && !((definedEx(CONFIG_X86_64) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && definedEx(CONFIG_X86_P6_NOP) && !(definedEx(CONFIG_MK7)) && !((definedEx(CONFIG_MK7) && !((!(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_P6_NOP))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_64))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_64)))))))
".byte 0x0f,0x1f,0x00\n"
#endif
#if (!((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && definedEx(CONFIG_X86_64) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)) && !((definedEx(CONFIG_MK7) && !((!(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_P6_NOP))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_64))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_64)))))) && !((!((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && !((definedEx(CONFIG_X86_64) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && definedEx(CONFIG_X86_P6_NOP) && !(definedEx(CONFIG_MK7)))))
".byte 0x66,0x66,0x90\n"
#endif
#if (!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_MK7) && !((!(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_P6_NOP))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && definedEx(CONFIG_X86_64))) && !((!(definedEx(CONFIG_X86_P6_NOP)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_64)))))) && !((!((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && !((definedEx(CONFIG_X86_64) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && definedEx(CONFIG_X86_P6_NOP) && !(definedEx(CONFIG_MK7)))) && !((!((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))) && definedEx(CONFIG_X86_64) && !(definedEx(CONFIG_MK7)) && !(definedEx(CONFIG_X86_P6_NOP)))))
".byte 0x8d,0x76,0x00\n"
#endif
 "\n662:\n" ".section .altinstructions,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661b\n" " " ".long" " " "663f\n" "	 .byte " "(3*32+18)" "\n" "	 .byte 662b-661b\n" "	 .byte 664f-663f\n" "	 .byte 0xff + (664f-663f) - (662b-661b)\n" ".previous\n" ".section .altinstr_replacement, \"ax\"\n" "663:\n\t" "lfence" "\n664:\n" ".previous" : : : "memory");
}


#line 19 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page.h" 1










 





				   

	



				  

	


































#line 20 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable_types.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/const.h" 1
/* const.h: Macros for dealing with constants.  */
















 




#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable_types.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page_types.h" 1




































 









					 


				




#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable_types.h" 2





















/* If _PAGE_BIT_PRESENT is clear, we use these: */
/* - if the user mapped it with PROT_NONE; pte_present gives true */

/* - set: nonlinear file mapping, saved PTE; unset:swap */



















#if definedEx(CONFIG_KMEMCHECK)

#endif
#if !(definedEx(CONFIG_KMEMCHECK))

#endif
#if (definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE))

#endif
#if !((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE)))

#endif








/* Set of bits not changed in pte_modify */

































































/*         xwr */


















/*
 * early identity mapping  pte attrib macros.
 */
#if definedEx(CONFIG_X86_64)

#endif
#if !(definedEx(CONFIG_X86_64))
/*
 * For PDE_IDENT_ATTR include USER bit. As the PDE and PTE protection
 * bits are combined, this will alow user to access the high address mapped
 * VDSO in the presence of CONFIG_COMPAT_VDSO
 */



#endif

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable_32_types.h" 1



/*
 * The Linux x86 paging architecture is 'compile-time dual-mode', it
 * implements both the traditional 2-level x86 page tables and the
 * newer 3-level PAE-mode page tables.
 */
#if definedEx(CONFIG_X86_PAE)
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable-3level_types.h" 1




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable-3level_types.h" 2

typedef u64	pteval_t;
typedef u64	pmdval_t;
typedef u64	pudval_t;
typedef u64	pgdval_t;
typedef u64	pgprotval_t;

typedef union {
	struct {
		unsigned long pte_low, pte_high;
	};
	pteval_t pte;
} pte_t;



#if 1

#endif


/*
 * PGDIR_SHIFT determines what a top-level page table entry can map
 */



/*
 * PMD_SHIFT determines the size of the area a middle-level
 * page table can map
 */



/*
 * entries per page directory level
 */




#line 12 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable_32_types.h" 2


#endif
#if !(definedEx(CONFIG_X86_PAE))
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable-2level_types.h" 1




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable-2level_types.h" 2

typedef unsigned long	pteval_t;
typedef unsigned long	pmdval_t;
typedef unsigned long	pudval_t;
typedef unsigned long	pgdval_t;
typedef unsigned long	pgprotval_t;

typedef union {
	pteval_t pte;
	pteval_t pte_low;
} pte_t;




/*
 * traditional i386 two-level paging structure:
 */





/*
 * the i386 is two-level, so we don't really have any
 * PMD directory physically.
 */




#line 16 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable_32_types.h" 2
#endif



/* Just any arbitrary offset to the start of the vmalloc VM area: the
 * current 8MB value just means that there will be a 8MB "hole" after the
 * physical memory until the kernel virtual memory starts.  That means that
 * any out-of-bounds memory accesses will hopefully be caught.
 * The vmalloc() routines leaves a hole of 4kB between each vmalloced
 * area for the same reason. ;)
 */



extern bool __vmalloc_start_set; /* set once high_memory is set */


#if definedEx(CONFIG_X86_PAE)

#endif
#if !(definedEx(CONFIG_X86_PAE))

#endif





 








#line 174 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable_types.h" 2
 


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 181 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable_types.h" 2

/* PTE_PFN_MASK extracts the PFN from a (pte|pmd|pud|pgd)val_t */


/* PTE_FLAGS_MASK extracts the flags from a (pte|pmd|pud|pgd)val_t */


typedef struct pgprot { pgprotval_t pgprot; } pgprot_t;

typedef struct { pgdval_t pgd; } pgd_t;

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pgd_t native_make_pgd(pgdval_t val)
{
	return (pgd_t) { val };
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pgdval_t native_pgd_val(pgd_t pgd)
{
	return pgd.pgd;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pgdval_t pgd_flags(pgd_t pgd)
{
	return native_pgd_val(pgd) & (~((pteval_t)(((signed long)(~(((1UL) << 12)-1))) & ((phys_addr_t)(1ULL << 
#if (definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_X86_PAE))) && !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)))))
46
#endif
#if (!((!(definedEx(CONFIG_X86_PAE)) && !(definedEx(CONFIG_X86_64)))) && definedEx(CONFIG_X86_PAE) && !(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_X86_PAE))) && !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)))))))
44
#endif
#if (!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)) && !((definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_X86_PAE))) && !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)))))) && !((!((!(definedEx(CONFIG_X86_PAE)) && !(definedEx(CONFIG_X86_64)))) && definedEx(CONFIG_X86_PAE) && !(definedEx(CONFIG_X86_64)))))
32
#endif
) - 1))));
}






	




	

#if 1
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/pgtable-nopud.h" 1






/*
 * Having the pud type consist of a pgd gets the size right, and allows
 * us to conceptually access the pgd entry that this pud is folded into
 * without casting.
 */
typedef struct { pgd_t pgd; } pud_t;






/*
 * The "pgd_xxx()" functions here are trivial for a folded two-level
 * setup: the pud is never bad, and a pud always exists (as it's folded
 * into the pgd entry)
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int pgd_none(pgd_t pgd)		{ return 0; }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int pgd_bad(pgd_t pgd)		{ return 0; }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int pgd_present(pgd_t pgd)	{ return 1; }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void pgd_clear(pgd_t *pgd)	{ }



/*
 * (puds are folded into pgds so this doesn't get actually called,
 * but the define is needed for a generic inline function.)
 */


static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pud_t * pud_offset(pgd_t * pgd, unsigned long address)
{
	return (pud_t *)pgd;
}







/*
 * allocating and freeing a pud is trivial: the 1-entry pud is
 * inside the pgd, so has no extra memory associated with it.
 */









#line 221 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable_types.h" 2

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pudval_t native_pud_val(pud_t pud)
{
	return native_pgd_val(pud.pgd);
}
#endif
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_X86_PAE))
typedef struct { pmdval_t pmd; } pmd_t;

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pmd_t native_make_pmd(pmdval_t val)
{
	return (pmd_t) { val };
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pmdval_t native_pmd_val(pmd_t pmd)
{
	return pmd.pmd;
}
#endif
#if !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_X86_PAE)))
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/pgtable-nopmd.h" 1




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/pgtable-nopud.h" 1






































	





















#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/pgtable-nopmd.h" 2

struct mm_struct;



/*
 * Having the pmd type consist of a pud gets the size right, and allows
 * us to conceptually access the pud entry that this pmd is folded into
 * without casting.
 */
typedef struct { pud_t pud; } pmd_t;






/*
 * The "pud_xxx()" functions here are trivial for a folded two-level
 * setup: the pmd is never bad, and a pmd always exists (as it's folded
 * into the pud entry)
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int pud_none(pud_t pud)		{ return 0; }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int pud_bad(pud_t pud)		{ return 0; }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int pud_present(pud_t pud)	{ return 1; }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void pud_clear(pud_t *pud)	{ }




/*
 * (pmds are folded into puds so this doesn't get actually called,
 * but the define is needed for a generic inline function.)
 */


static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pmd_t * pmd_offset(pud_t * pud, unsigned long address)
{
	return (pmd_t *)pud;
}







/*
 * allocating and freeing a pmd is trivial: the 1-entry pmd is
 * inside the pud, so has no extra memory associated with it.
 */

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void pmd_free(struct mm_struct *mm, pmd_t *pmd)
{
}







#line 242 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable_types.h" 2

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pmdval_t native_pmd_val(pmd_t pmd)
{
	return native_pgd_val(pmd.pud.pgd);
}
#endif
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pudval_t pud_flags(pud_t pud)
{
	return native_pud_val(pud) & (~((pteval_t)(((signed long)(~(((1UL) << 12)-1))) & ((phys_addr_t)(1ULL << 
#if (definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_X86_PAE))) && !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)))))
46
#endif
#if (!((!(definedEx(CONFIG_X86_PAE)) && !(definedEx(CONFIG_X86_64)))) && definedEx(CONFIG_X86_PAE) && !(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_X86_PAE))) && !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)))))))
44
#endif
#if (!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)) && !((definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_X86_PAE))) && !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)))))) && !((!((!(definedEx(CONFIG_X86_PAE)) && !(definedEx(CONFIG_X86_64)))) && definedEx(CONFIG_X86_PAE) && !(definedEx(CONFIG_X86_64)))))
32
#endif
) - 1))));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pmdval_t pmd_flags(pmd_t pmd)
{
	return native_pmd_val(pmd) & (~((pteval_t)(((signed long)(~(((1UL) << 12)-1))) & ((phys_addr_t)(1ULL << 
#if (definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_X86_PAE))) && !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)))))
46
#endif
#if (!((!(definedEx(CONFIG_X86_PAE)) && !(definedEx(CONFIG_X86_64)))) && definedEx(CONFIG_X86_PAE) && !(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_X86_PAE))) && !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)))))))
44
#endif
#if (!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)) && !((definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_X86_PAE))) && !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)))))) && !((!((!(definedEx(CONFIG_X86_PAE)) && !(definedEx(CONFIG_X86_64)))) && definedEx(CONFIG_X86_PAE) && !(definedEx(CONFIG_X86_64)))))
32
#endif
) - 1))));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pte_t native_make_pte(pteval_t val)
{
	return (pte_t) { .pte = val };
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pteval_t native_pte_val(pte_t pte)
{
	return pte.pte;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pteval_t pte_flags(pte_t pte)
{
	return native_pte_val(pte) & (~((pteval_t)(((signed long)(~(((1UL) << 12)-1))) & ((phys_addr_t)(1ULL << 
#if (definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_X86_PAE))) && !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)))))
46
#endif
#if (!((!(definedEx(CONFIG_X86_PAE)) && !(definedEx(CONFIG_X86_64)))) && definedEx(CONFIG_X86_PAE) && !(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_X86_PAE))) && !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)))))))
44
#endif
#if (!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)) && !((definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_X86_64)) && definedEx(CONFIG_X86_PAE))) && !((!(definedEx(CONFIG_X86_64)) && !(definedEx(CONFIG_X86_PAE)))))) && !((!((!(definedEx(CONFIG_X86_PAE)) && !(definedEx(CONFIG_X86_64)))) && definedEx(CONFIG_X86_PAE) && !(definedEx(CONFIG_X86_64)))))
32
#endif
) - 1))));
}





typedef struct page *pgtable_t;

extern pteval_t __supported_pte_mask;
extern void set_nx(void);
extern int nx_enabled;


extern pgprot_t pgprot_writecombine(pgprot_t prot);

/* Indicate that x86 has its own track and untrack pfn vma functions */



struct file;
pgprot_t phys_mem_access_prot(struct file *file, unsigned long pfn,
                              unsigned long size, pgprot_t vma_prot);
int phys_mem_access_prot_allowed(struct file *file, unsigned long pfn,
                              unsigned long size, pgprot_t *vma_prot);

/* Install a pte for a particular vaddr in kernel space. */
void set_pte_vaddr(unsigned long vaddr, pte_t pte);


extern void native_pagetable_setup_start(pgd_t *base);
extern void native_pagetable_setup_done(pgd_t *base);
 


struct seq_file;
extern void arch_report_meminfo(struct seq_file *m);

enum {
	PG_LEVEL_NONE,
	PG_LEVEL_4K,
	PG_LEVEL_2M,
	PG_LEVEL_1G,
	PG_LEVEL_NUM
};


extern void update_page_count(int level, unsigned long pages);
 

/*
 * Helper function that returns the kernel pagetable entry controlling
 * the virtual address 'address'. NULL means no pagetable entry present.
 * NOTE: the return type is pte_t but if the pmd is PSE then we return it
 * as a pte too.
 */
extern pte_t *lookup_address(unsigned long address, unsigned int *level);



#line 21 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/percpu.h" 1






 




















 





 

 





 













 















































































































































































































 














#line 22 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/msr.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/msr-index.h" 1



/* CPU model specific register (MSR) numbers */

/* x86-64 specific MSRs */










/* EFER bits: */














/* Intel MSRs. Some also available on other CPUs */








 






 

 













 

 
 
 

	





	




	




	





















 















 





 




















































 







	










	




	




	














	














	




	




	




	




	




	









	




	




	




	




	




















#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/memory_hotplug.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/notifier.h" 1
/*
 *	Routines to manage notifier chains for passing status changes to any
 *	interested routines. We need this instead of hard coded call lists so
 *	that modules can poke their nose into the innards. The network devices
 *	needed them so here they are for the rest of you.
 *
 *				Alan Cox <Alan.Cox@linux.org>
 */
 


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/errno.h" 1
































#line 14 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/notifier.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mutex.h" 1
/*
 * Mutexes: blocking mutual exclusion locks
 *
 * started by Ingo Molnar:
 *
 *  Copyright (C) 2004, 2005, 2006 Red Hat, Inc., Ingo Molnar <mingo@redhat.com>
 *
 * This file contains the main data structure and API definitions.
 */



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/list.h" 1



















	









	
	










			      
			      

	
	
	
	

 
			      
			      











	













	











	
	











	
	
	

 









				

	
	
	
	



					

	
	








	
	









	
	








				  

	
	








				

	








	

















	
	








	



		

	
	
	
	
	
	
	

















		

	
		
	
		
		
	
		
	
		



				 
				 

	
	

	
	

	
	








				

	
		








				

	
		










				    

	
		
		
	











					 

	
		
		
	























































































































































































































	



	







	
	




	




	




	
	
	
	
		




	
	
	




	
		
		
	




	
	
	
		
	
	




					

	
	
	
	



					

	
	
	

	
		







				   

	
	
		
	































































#line 15 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mutex.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/spinlock_types.h" 1













 




	

	


	
	


	









 






 













	
		



		
			
			
		

	





















#line 16 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mutex.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/linkage.h" 1








 














































































#line 17 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mutex.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/lockdep.h" 1
/*
 * Runtime locking correctness validator
 *
 *  Copyright (C) 2006,2007 Red Hat, Inc., Ingo Molnar <mingo@redhat.com>
 *  Copyright (C) 2007 Red Hat, Inc., Peter Zijlstra <pzijlstr@redhat.com>
 *
 * see Documentation/lockdep-design.txt for more details.
 */


























	



	








	


	

	


	

	
	
	

	


	
	

	




	

	



	

	


	

	
	


	
	





	
	
	
	



	
	
	
	
	

	
	



	
	
	
	
	
	
	










	
	
	

	
	








	
	
	
	

	



	






	
	
	
	
	











	













	
	
	
	

	
	

	
	












	
	

	
	
	
	






















			     





























				    

	


















			 
			 


			 






			   
			   


		

	












 

























































 















 





 







 

























 



 






 



 






 


 






 



 






 


 















 



#line 18 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mutex.h" 2

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/atomic.h" 1

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/atomic_32.h" 1























	











	











	
		     
		     











	
		     
		     













	

	
		     
		     
	










	
		     










	
		     












	

	
		     
		     
	












	

	
		     
		     
	













	

	
		     
		     
	











	

	
	
		

	
	
	
		     
		     
	



	
	
	
	
	












	




	




	













	
	
	
		
			
		
		
			
		
	
	

























	

































	

	






	
		
		
		
			
			
		

	


































































































#line 4 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/atomic.h" 2
 

#line 20 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mutex.h" 2

/*
 * Simple, straightforward mutexes with strict semantics:
 *
 * - only one task can hold the mutex at a time
 * - only the owner can unlock the mutex
 * - multiple unlocks are not permitted
 * - recursive locking is not permitted
 * - a mutex object must be initialized via the API
 * - a mutex object must not be initialized via memset or copying
 * - task may not exit with mutex held
 * - memory areas where held locks reside must not be freed
 * - held mutexes must not be reinitialized
 * - mutexes may not be used in hardware or software interrupt
 *   contexts such as tasklets and timers
 *
 * These semantics are fully enforced when DEBUG_MUTEXES is
 * enabled. Furthermore, besides enforcing the above rules, the mutex
 * debugging code also implements a number of additional features
 * that make lock debugging easier and faster:
 *
 * - uses symbolic names of mutexes, whenever they are printed in debug output
 * - point-of-acquire tracking, symbolic lookup of function names
 * - list of all locks held in the system, printout of them
 * - owner tracking
 * - detects self-recursing locks and prints out all relevant info
 * - detects multi-task circular deadlocks and prints out all affected
 *   locks and tasks (and only those tasks)
 */

 typedef int spinlock_t  ;
struct mutex {
	/* 1: unlocked, 0: locked, negative: locked, possible waiters */
	atomic_t		count;
	spinlock_t		wait_lock;
	struct list_head	wait_list;
#if (definedEx(CONFIG_DEBUG_MUTEXES) || definedEx(CONFIG_SMP))
	struct thread_info	*owner;
#endif
#if definedEx(CONFIG_DEBUG_MUTEXES)
	const char 		*name;
	void			*magic;
#endif
#if definedEx(CONFIG_DEBUG_LOCK_ALLOC)
	struct lockdep_map	dep_map;
#endif
};

/*
 * This is the control structure for tasks blocked on mutex,
 * which resides on the blocked task's kernel stack:
 */
struct mutex_waiter {
	struct list_head	list;
	struct task_struct	*task;
#if definedEx(CONFIG_DEBUG_MUTEXES)
	void			*magic;
#endif
};

#if definedEx(CONFIG_DEBUG_MUTEXES)
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mutex-debug.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/linkage.h" 1








 














































































#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mutex-debug.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/lockdep.h" 1
/*
 * Runtime locking correctness validator
 *
 *  Copyright (C) 2006,2007 Red Hat, Inc., Ingo Molnar <mingo@redhat.com>
 *  Copyright (C) 2007 Red Hat, Inc., Peter Zijlstra <pzijlstr@redhat.com>
 *
 * see Documentation/lockdep-design.txt for more details.
 */


























	



	








	


	

	


	

	
	
	

	


	
	

	




	

	



	

	


	

	
	


	
	





	
	
	
	



	
	
	
	
	

	
	



	
	
	
	
	
	
	










	
	
	

	
	








	
	
	
	

	



	






	
	
	
	
	











	













	
	
	
	

	
	

	
	












	
	

	
	
	
	






















			     





























				    

	


















			 
			 


			 






			   
			   


		

	












 

























































 















 





 







 

























 



 






 



 






 


 






 



 






 


 















 



#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mutex-debug.h" 2

/*
 * Mutexes - debugging helpers:
 */











extern void mutex_destroy(struct mutex *lock);


#line 80 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mutex.h" 2
#endif
#if !(definedEx(CONFIG_DEBUG_MUTEXES))








#endif
#if definedEx(CONFIG_DEBUG_LOCK_ALLOC)


#endif
#if !(definedEx(CONFIG_DEBUG_LOCK_ALLOC))

#endif










extern void __mutex_init(struct mutex *lock, const char *name,
			 struct lock_class_key *key);

/**
 * mutex_is_locked - is the mutex locked
 * @lock: the mutex to be queried
 *
 * Returns 1 if the mutex is locked, 0 if unlocked.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int mutex_is_locked(struct mutex *lock)
{
	return atomic_read(&lock->count) != 1;
}

/*
 * See kernel/mutex.c for detailed documentation of these APIs.
 * Also see Documentation/mutex-design.txt.
 */
#if definedEx(CONFIG_DEBUG_LOCK_ALLOC)
extern void mutex_lock_nested(struct mutex *lock, unsigned int subclass);
extern int 
#if (definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))
__attribute__((warn_unused_result))
#endif
#if (!(definedEx(CONFIG_ENABLE_MUST_CHECK)) && !((definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))))

#endif
 mutex_lock_interruptible_nested(struct mutex *lock,
					unsigned int subclass);
extern int 
#if (definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))
__attribute__((warn_unused_result))
#endif
#if (!(definedEx(CONFIG_ENABLE_MUST_CHECK)) && !((definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))))

#endif
 mutex_lock_killable_nested(struct mutex *lock,
					unsigned int subclass);




#endif
#if !(definedEx(CONFIG_DEBUG_LOCK_ALLOC))
extern void mutex_lock(struct mutex *lock);
extern int 
#if (definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))
__attribute__((warn_unused_result))
#endif
#if (!(definedEx(CONFIG_ENABLE_MUST_CHECK)) && !((definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))))

#endif
 mutex_lock_interruptible(struct mutex *lock);
extern int 
#if (definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))
__attribute__((warn_unused_result))
#endif
#if (!(definedEx(CONFIG_ENABLE_MUST_CHECK)) && !((definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))))

#endif
 mutex_lock_killable(struct mutex *lock);




#endif
/*
 * NOTE: mutex_trylock() follows the spin_trylock() convention,
 *       not the down_trylock() convention!
 *
 * Returns 1 if the mutex has been acquired successfully, and 0 on contention.
 */
extern int mutex_trylock(struct mutex *lock);
extern void mutex_unlock(struct mutex *lock);
extern int atomic_dec_and_mutex_lock(atomic_t *cnt, struct mutex *lock);


#line 15 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/notifier.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/rwsem.h" 1
/* rwsem.h: R/W semaphores, public interface
 *
 * Written by David Howells (dhowells@redhat.com).
 * Derived from asm-i386/semaphore.h
 */




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/linkage.h" 1








 














































































#line 12 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/rwsem.h" 2

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 14 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/rwsem.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 1





























































 
























































 


  












   











 

	





	




	

	









	

	

	

	

	

	

	



	

	







































	

	




				   
















 
	


	

















	





	




	
		

















	
	
	
	
	
	

















	
	
	


				
				

				
				

			







	
	
	


























 






 


 
















 





















 




























 






















































	



	



























 











	




	

































































































































	
	
	
	
	
	
	
	
	
	
	
	
	
	

























 






#line 15 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/rwsem.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/system.h" 1















 



				


		      











 



























































 


















 



































































 








	
	
	




	













	
	
	




	




	
	
	




	




	
	
	




	




	
	
	




	
	


	
		     
		     
		     
 	

	




	





	
	
	




	




	




 























	





























 





























































 



 



 














	
	



#line 16 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/rwsem.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/atomic.h" 1

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/atomic_32.h" 1























	











	











	
		     
		     











	
		     
		     













	

	
		     
		     
	










	
		     










	
		     












	

	
		     
		     
	












	

	
		     
		     
	













	

	
		     
		     
	











	

	
	
		

	
	
	
		     
		     
	



	
	
	
	
	












	




	




	













	
	
	
		
			
		
		
			
		
	
	

























	

































	

	






	
		
		
		
			
			
		

	


































































































#line 4 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/atomic.h" 2
 

#line 17 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/rwsem.h" 2

struct rw_semaphore;

#if definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/rwsem-spinlock.h" 1
/* rwsem-spinlock.h: fallback C implementation
 *
 * Copyright (c) 2001   David Howells (dhowells@redhat.com).
 * - Derived partially from ideas by Andrea Arcangeli <andrea@suse.de>
 * - Derived also from comments by Linus
 */







#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/spinlock.h" 1






















































































 


  
				   







 






 

 













 

 
 
 

	





	




	




	





















 















 





 




















































 







	










	




	




	














	














	




	




	




	




	




	









	




	




	




	




	




















#line 17 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/rwsem-spinlock.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/list.h" 1



















	









	
	










			      
			      

	
	
	
	

 
			      
			      











	













	











	
	











	
	
	

 









				

	
	
	
	



					

	
	








	
	









	
	








				  

	
	








				

	








	

















	
	








	



		

	
	
	
	
	
	
	

















		

	
		
	
		
		
	
		
	
		



				 
				 

	
	

	
	

	
	








				

	
		








				

	
		










				    

	
		
		
	











					 

	
		
		
	























































































































































































































	



	







	
	




	




	




	
	
	
	
		




	
	
	




	
		
		
	




	
	
	
		
	
	




					

	
	
	
	



					

	
	
	

	
		







				   

	
	
		
	































































#line 18 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/rwsem-spinlock.h" 2


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 22 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/rwsem-spinlock.h" 2

struct rwsem_waiter;

/*
 * the rw-semaphore definition
 * - if activity is 0 then there are no active readers or writers
 * - if activity is +ve then that is the number of active readers
 * - if activity is -1 then there is one active writer
 * - if wait_list is not empty, then there are processes waiting for the semaphore
 */
struct rw_semaphore {
	__s32			activity;
	spinlock_t		wait_lock;
	struct list_head	wait_list;
#if definedEx(CONFIG_DEBUG_LOCK_ALLOC)
	struct lockdep_map dep_map;
#endif
};

#if definedEx(CONFIG_DEBUG_LOCK_ALLOC)

#endif
#if !(definedEx(CONFIG_DEBUG_LOCK_ALLOC))

#endif







extern void __init_rwsem(struct rw_semaphore *sem, const char *name,
			 struct lock_class_key *key);








extern void __down_read(struct rw_semaphore *sem);
extern int __down_read_trylock(struct rw_semaphore *sem);
extern void __down_write(struct rw_semaphore *sem);
extern void __down_write_nested(struct rw_semaphore *sem, int subclass);
extern int __down_write_trylock(struct rw_semaphore *sem);
extern void __up_read(struct rw_semaphore *sem);
extern void __up_write(struct rw_semaphore *sem);
extern void __downgrade_write(struct rw_semaphore *sem);
extern int rwsem_is_locked(struct rw_semaphore *sem);



#line 22 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/rwsem.h" 2
#endif
#if !(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK))
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/rwsem.h" 1
/* rwsem.h: R/W semaphores implemented using XADD/CMPXCHG for i486+
 *
 * Written by David Howells (dhowells@redhat.com).
 *
 * Derived from asm-x86/semaphore.h
 *
 *
 * The MSW of the count is the negated number of active writers and waiting
 * lockers, and the LSW is the total number of active locks
 *
 * The lock count is initialized to 0 (no active and no waiting lockers).
 *
 * When a writer subtracts WRITE_BIAS, it'll get 0xffff0001 for the case of an
 * uncontended lock. This can be determined because XADD returns the old value.
 * Readers increment by 1 and see a positive value when uncontended, negative
 * if there are writers (and maybe) readers waiting (in which case it goes to
 * sleep).
 *
 * The value of WAITING_BIAS supports up to 32766 waiting processes. This can
 * be extended to 65534 by manually checking the whole MSW rather than relying
 * on the S flag.
 *
 * The value of ACTIVE_BIAS supports up to 65535 active processes.
 *
 * This should be totally fair - if anything is waiting, a process that wants a
 * lock will go to the back of the queue. When the currently active lock is
 * released, if there's a writer at the front of the queue, then that and only
 * that will be woken up; if there's a bunch of consequtive readers at the
 * front, then they'll all be woken up, but no other readers will be.
 */








#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/list.h" 1



















	









	
	










			      
			      

	
	
	
	

 
			      
			      











	













	











	
	











	
	
	

 









				

	
	
	
	



					

	
	








	
	









	
	








				  

	
	








				

	








	

















	
	








	



		

	
	
	
	
	
	
	

















		

	
		
	
		
		
	
		
	
		



				 
				 

	
	

	
	

	
	








				

	
		








				

	
		










				    

	
		
		
	











					 

	
		
		
	























































































































































































































	



	







	
	




	




	




	
	
	
	
		




	
	
	




	
		
		
	




	
	
	
		
	
	




					

	
	
	
	



					

	
	
	

	
		







				   

	
	
		
	































































#line 43 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/rwsem.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/spinlock.h" 1






















































































 


  
				   







 






 

 













 

 
 
 

	





	




	




	





















 















 





 




















































 







	










	




	




	














	














	




	




	




	




	




	









	




	




	




	




	




















#line 44 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/rwsem.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/lockdep.h" 1
/*
 * Runtime locking correctness validator
 *
 *  Copyright (C) 2006,2007 Red Hat, Inc., Ingo Molnar <mingo@redhat.com>
 *  Copyright (C) 2007 Red Hat, Inc., Peter Zijlstra <pzijlstr@redhat.com>
 *
 * see Documentation/lockdep-design.txt for more details.
 */


























	



	








	


	

	


	

	
	
	

	


	
	

	




	

	



	

	


	

	
	


	
	





	
	
	
	



	
	
	
	
	

	
	



	
	
	
	
	
	
	










	
	
	

	
	








	
	
	
	

	



	






	
	
	
	
	











	













	
	
	
	

	
	

	
	












	
	

	
	
	
	






















			     





























				    

	


















			 
			 


			 






			   
			   


		

	












 

























































 















 





 







 

























 



 






 



 






 


 






 



 






 


 















 



#line 45 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/rwsem.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/asm.h" 1






 




 






























 






#line 46 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/rwsem.h" 2

struct rwsem_waiter;

extern __attribute__((regparm(3))) struct rw_semaphore *
 rwsem_down_read_failed(struct rw_semaphore *sem);
extern __attribute__((regparm(3))) struct rw_semaphore *
 rwsem_down_write_failed(struct rw_semaphore *sem);
extern __attribute__((regparm(3))) struct rw_semaphore *
 rwsem_wake(struct rw_semaphore *);
extern __attribute__((regparm(3))) struct rw_semaphore *
 rwsem_downgrade_wake(struct rw_semaphore *sem);

/*
 * the semaphore definition
 *
 * The bias values and the counter type limits the number of
 * potential readers/writers to 32767 for 32 bits and 2147483647
 * for 64 bits.
 */

#if definedEx(CONFIG_X86_64)

#endif
#if !(definedEx(CONFIG_X86_64))

#endif






typedef signed long rwsem_count_t;

struct rw_semaphore {
	rwsem_count_t		count;
	spinlock_t		wait_lock;
	struct list_head	wait_list;
#if definedEx(CONFIG_DEBUG_LOCK_ALLOC)
	struct lockdep_map dep_map;
#endif
};

#if definedEx(CONFIG_DEBUG_LOCK_ALLOC)

#endif
#if !(definedEx(CONFIG_DEBUG_LOCK_ALLOC))

#endif









extern void __init_rwsem(struct rw_semaphore *sem, const char *name,
			 struct lock_class_key *key);








/*
 * lock for reading
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __down_read(struct rw_semaphore *sem)
{
	asm volatile("# beginning down_read\n\t"
		     
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 " " "incl" " " "(%1)\n\t"
		     /* adds 0x00000001, returns the old value */
		     "  jns        1f\n"
		     "  call call_rwsem_down_read_failed\n"
		     "1:\n\t"
		     "# ending down_read\n\t"
		     : "+m" (sem->count)
		     : "a" (sem)
		     : "memory", "cc");
}

/*
 * trylock for reading -- returns 1 if successful, 0 if contention
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int __down_read_trylock(struct rw_semaphore *sem)
{
	rwsem_count_t result, tmp;
	asm volatile("# beginning __down_read_trylock\n\t"
		     "  mov          %0,%1\n\t"
		     "1:\n\t"
		     "  mov          %1,%2\n\t"
		     "  add          %3,%2\n\t"
		     "  jle	     2f\n\t"
		     
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "  cmpxchg  %2,%0\n\t"
		     "  jnz	     1b\n\t"
		     "2:\n\t"
		     "# ending __down_read_trylock\n\t"
		     : "+m" (sem->count), "=&a" (result), "=&r" (tmp)
		     : "i" (0x00000001L)
		     : "memory", "cc");
	return result >= 0 ? 1 : 0;
}

/*
 * lock for writing
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __down_write_nested(struct rw_semaphore *sem, int subclass)
{
	rwsem_count_t tmp;

	tmp = ((-
#if (!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && !(definedEx(CONFIG_X86_64)))))
0xffffffffL
#endif
#if (!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && !(definedEx(CONFIG_X86_64)) && !((!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && !(definedEx(CONFIG_X86_64)))))))
0x0000ffffL
#endif
-1) + 0x00000001L);
	asm volatile("# beginning down_write\n\t"
		     
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "  xadd      %1,(%2)\n\t"
		     /* subtract 0x0000ffff, returns the old value */
		     "  test      %1,%1\n\t"
		     /* was the count 0 before? */
		     "  jz        1f\n"
		     "  call call_rwsem_down_write_failed\n"
		     "1:\n"
		     "# ending down_write"
		     : "+m" (sem->count), "=d" (tmp)
		     : "a" (sem), "1" (tmp)
		     : "memory", "cc");
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __down_write(struct rw_semaphore *sem)
{
	__down_write_nested(sem, 0);
}

/*
 * trylock for writing -- returns 1 if successful, 0 if contention
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int __down_write_trylock(struct rw_semaphore *sem)
{
	rwsem_count_t ret = ({ __typeof__(*(((&sem->count)))) __ret; __typeof__(*(((&sem->count)))) __old = (((
 0x00000000L))); __typeof__(*(((&sem->count)))) __new = (((
((-
#if (!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && !(definedEx(CONFIG_X86_64)))))
0xffffffffL
#endif
#if (!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && !(definedEx(CONFIG_X86_64)) && !((!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && !(definedEx(CONFIG_X86_64)))))))
0x0000ffffL
#endif
-1) + 0x00000001L)))); switch ((sizeof(*&sem->count))) { case 1: asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "cmpxchgb %b1,%2" : "=a"(__ret) : "q"(__new), "m"(*((struct __xchg_dummy *)(((&sem->count))))), "0"(__old) : "memory"); break; case 2: asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "cmpxchgw %w1,%2" : "=a"(__ret) : "r"(__new), "m"(*((struct __xchg_dummy *)(((&sem->count))))), "0"(__old) : "memory"); break; case 4: asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "cmpxchgl %1,%2" : "=a"(__ret) : "r"(__new), "m"(*((struct __xchg_dummy *)(((&sem->count))))), "0"(__old) : "memory"); break; default: __cmpxchg_wrong_size(); } __ret; });
	if (ret == 0x00000000L)
		return 1;
	return 0;
}

/*
 * unlock after reading
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __up_read(struct rw_semaphore *sem)
{
	rwsem_count_t tmp = -0x00000001L;
	asm volatile("# beginning __up_read\n\t"
		     
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "  xadd      %1,(%2)\n\t"
		     /* subtracts 1, returns the old value */
		     "  jns        1f\n\t"
		     "  call call_rwsem_wake\n"
		     "1:\n"
		     "# ending __up_read\n"
		     : "+m" (sem->count), "=d" (tmp)
		     : "a" (sem), "1" (tmp)
		     : "memory", "cc");
}

/*
 * unlock after writing
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __up_write(struct rw_semaphore *sem)
{
	rwsem_count_t tmp;
	asm volatile("# beginning __up_write\n\t"
		     
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "  xadd      %1,(%2)\n\t"
		     /* tries to transition
			0xffff0001 -> 0x00000000 */
		     "  jz       1f\n"
		     "  call call_rwsem_wake\n"
		     "1:\n\t"
		     "# ending __up_write\n"
		     : "+m" (sem->count), "=d" (tmp)
		     : "a" (sem), "1" (-((-
#if (!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && !(definedEx(CONFIG_X86_64)))))
0xffffffffL
#endif
#if (!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && !(definedEx(CONFIG_X86_64)) && !((!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && !(definedEx(CONFIG_X86_64)))))))
0x0000ffffL
#endif
-1) + 0x00000001L))
		     : "memory", "cc");
}

/*
 * downgrade write lock to read lock
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __downgrade_write(struct rw_semaphore *sem)
{
	asm volatile("# beginning __downgrade_write\n\t"
		     
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 " " "addl" " " "%2,(%1)\n\t"
		     /*
		      * transitions 0xZZZZ0001 -> 0xYYYY0001 (i386)
		      *     0xZZZZZZZZ00000001 -> 0xYYYYYYYY00000001 (x86_64)
		      */
		     "  jns       1f\n\t"
		     "  call call_rwsem_downgrade_wake\n"
		     "1:\n\t"
		     "# ending __downgrade_write\n"
		     : "+m" (sem->count)
		     : "a" (sem), "er" (-(-
#if (!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && !(definedEx(CONFIG_X86_64)))))
0xffffffffL
#endif
#if (!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && !(definedEx(CONFIG_X86_64)) && !((!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && definedEx(CONFIG_X86_64) && !((!(definedEx(CONFIG_RWSEM_GENERIC_SPINLOCK)) && !(definedEx(CONFIG_X86_64)))))))
0x0000ffffL
#endif
-1))
		     : "memory", "cc");
}

/*
 * implement atomic add functionality
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void rwsem_atomic_add(rwsem_count_t delta,
				    struct rw_semaphore *sem)
{
	asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 " " "addl" " " "%1,%0"
		     : "+m" (sem->count)
		     : "er" (delta));
}

/*
 * implement exchange and add functionality
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 rwsem_count_t rwsem_atomic_update(rwsem_count_t delta,
						struct rw_semaphore *sem)
{
	rwsem_count_t tmp = delta;

	asm volatile(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".section .smp_locks,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661f\n" ".previous\n" "661:\n\tlock; "
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
""
#endif
 "xadd %0,%1"
		     : "+r" (tmp), "+m" (sem->count)
		     : : "memory");

	return tmp + delta;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int rwsem_is_locked(struct rw_semaphore *sem)
{
	return (sem->count != 0);
}



#line 24 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/rwsem.h" 2
#endif
/*
 * lock for reading
 */
extern void down_read(struct rw_semaphore *sem);

/*
 * trylock for reading -- returns 1 if successful, 0 if contention
 */
extern int down_read_trylock(struct rw_semaphore *sem);

/*
 * lock for writing
 */
extern void down_write(struct rw_semaphore *sem);

/*
 * trylock for writing -- returns 1 if successful, 0 if contention
 */
extern int down_write_trylock(struct rw_semaphore *sem);

/*
 * release a read lock
 */
extern void up_read(struct rw_semaphore *sem);

/*
 * release a write lock
 */
extern void up_write(struct rw_semaphore *sem);

/*
 * downgrade write lock to read lock
 */
extern void downgrade_write(struct rw_semaphore *sem);

#if definedEx(CONFIG_DEBUG_LOCK_ALLOC)
/*
 * nested locking. NOTE: rwsems are not allowed to recurse
 * (which occurs if the same task tries to acquire the same
 * lock instance multiple times), but multiple locks of the
 * same lock class might be taken, if the order of the locks
 * is always the same. This ordering rule can be expressed
 * to lockdep via the _nested() APIs, but enumerating the
 * subclasses that are used. (If the nesting relationship is
 * static then another method for expressing nested locking is
 * the explicit definition of lock class keys and the use of
 * lockdep_set_class() at lock initialization time.
 * See Documentation/lockdep-design.txt for more details.)
 */
extern void down_read_nested(struct rw_semaphore *sem, int subclass);
extern void down_write_nested(struct rw_semaphore *sem, int subclass);
/*
 * Take/release a lock when not the owner will release it.
 *
 * [ This API should be avoided as much as possible - the
 *   proper abstraction for this case is completions. ]
 */
extern void down_read_non_owner(struct rw_semaphore *sem);
extern void up_read_non_owner(struct rw_semaphore *sem);
#endif
#if !(definedEx(CONFIG_DEBUG_LOCK_ALLOC))




#endif

#line 16 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/notifier.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/srcu.h" 1
/*
 * Sleepable Read-Copy Update mechanism for mutual exclusion
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * Copyright (C) IBM Corporation, 2006
 *
 * Author: Paul McKenney <paulmck@us.ibm.com>
 *
 * For detailed explanation of Read-Copy Update mechanism see -
 * 		Documentation/RCU/ *.txt
 *
 */




struct srcu_struct_array {
	int c[2];
};

struct srcu_struct {
	int completed;
	struct srcu_struct_array *per_cpu_ref;
	struct mutex mutex;
};

#if !(definedEx(CONFIG_PREEMPT))

#endif
#if definedEx(CONFIG_PREEMPT)

#endif
int init_srcu_struct(struct srcu_struct *sp);
void cleanup_srcu_struct(struct srcu_struct *sp);
int srcu_read_lock(struct srcu_struct *sp) ;
void srcu_read_unlock(struct srcu_struct *sp, int idx) ;
void synchronize_srcu(struct srcu_struct *sp);
void synchronize_srcu_expedited(struct srcu_struct *sp);
long srcu_batches_completed(struct srcu_struct *sp);


#line 17 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/notifier.h" 2

/*
 * Notifier chains are of four types:
 *
 *	Atomic notifier chains: Chain callbacks run in interrupt/atomic
 *		context. Callouts are not allowed to block.
 *	Blocking notifier chains: Chain callbacks run in process context.
 *		Callouts are allowed to block.
 *	Raw notifier chains: There are no restrictions on callbacks,
 *		registration, or unregistration.  All locking and protection
 *		must be provided by the caller.
 *	SRCU notifier chains: A variant of blocking notifier chains, with
 *		the same restrictions.
 *
 * atomic_notifier_chain_register() may be called from an atomic context,
 * but blocking_notifier_chain_register() and srcu_notifier_chain_register()
 * must be called from a process context.  Ditto for the corresponding
 * _unregister() routines.
 *
 * atomic_notifier_chain_unregister(), blocking_notifier_chain_unregister(),
 * and srcu_notifier_chain_unregister() _must not_ be called from within
 * the call chain.
 *
 * SRCU notifier chains are an alternative form of blocking notifier chains.
 * They use SRCU (Sleepable Read-Copy Update) instead of rw-semaphores for
 * protection of the chain links.  This means there is _very_ low overhead
 * in srcu_notifier_call_chain(): no cache bounces and no memory barriers.
 * As compensation, srcu_notifier_chain_unregister() is rather expensive.
 * SRCU notifier chains should be used when the chain will be called very
 * often but notifier_blocks will seldom be removed.  Also, SRCU notifier
 * chains are slightly more difficult to use because they require special
 * runtime initialization.
 */

struct notifier_block {
	int (*notifier_call)(struct notifier_block *, unsigned long, void *);
	struct notifier_block *next;
	int priority;
};

struct atomic_notifier_head {
	spinlock_t lock;
	struct notifier_block *head;
};

struct blocking_notifier_head {
	struct rw_semaphore rwsem;
	struct notifier_block *head;
};

struct raw_notifier_head {
	struct notifier_block *head;
};

struct srcu_notifier_head {
	struct mutex mutex;
	struct srcu_struct srcu;
	struct notifier_block *head;
};













/* srcu_notifier_heads must be initialized and cleaned up dynamically */
extern void srcu_init_notifier_head(struct srcu_notifier_head *nh);











/* srcu_notifier_heads cannot be initialized statically */












extern int atomic_notifier_chain_register(struct atomic_notifier_head *nh,
		struct notifier_block *nb);
extern int blocking_notifier_chain_register(struct blocking_notifier_head *nh,
		struct notifier_block *nb);
extern int raw_notifier_chain_register(struct raw_notifier_head *nh,
		struct notifier_block *nb);
extern int srcu_notifier_chain_register(struct srcu_notifier_head *nh,
		struct notifier_block *nb);

extern int blocking_notifier_chain_cond_register(
		struct blocking_notifier_head *nh,
		struct notifier_block *nb);

extern int atomic_notifier_chain_unregister(struct atomic_notifier_head *nh,
		struct notifier_block *nb);
extern int blocking_notifier_chain_unregister(struct blocking_notifier_head *nh,
		struct notifier_block *nb);
extern int raw_notifier_chain_unregister(struct raw_notifier_head *nh,
		struct notifier_block *nb);
extern int srcu_notifier_chain_unregister(struct srcu_notifier_head *nh,
		struct notifier_block *nb);

extern int atomic_notifier_call_chain(struct atomic_notifier_head *nh,
		unsigned long val, void *v);
extern int __atomic_notifier_call_chain(struct atomic_notifier_head *nh,
	unsigned long val, void *v, int nr_to_call, int *nr_calls);
extern int blocking_notifier_call_chain(struct blocking_notifier_head *nh,
		unsigned long val, void *v);
extern int __blocking_notifier_call_chain(struct blocking_notifier_head *nh,
	unsigned long val, void *v, int nr_to_call, int *nr_calls);
extern int raw_notifier_call_chain(struct raw_notifier_head *nh,
		unsigned long val, void *v);
extern int __raw_notifier_call_chain(struct raw_notifier_head *nh,
	unsigned long val, void *v, int nr_to_call, int *nr_calls);
extern int srcu_notifier_call_chain(struct srcu_notifier_head *nh,
		unsigned long val, void *v);
extern int __srcu_notifier_call_chain(struct srcu_notifier_head *nh,
	unsigned long val, void *v, int nr_to_call, int *nr_calls);





						/* Bad/Veto action */
/*
 * Clean way to return from the notifier and stop further calls.
 */


/* Encapsulate (negative) errno value (in particular, NOTIFY_BAD <=> EPERM). */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int notifier_from_errno(int err)
{
	return 0x8000 | (0x0001 - err);
}

/* Restore (negative) errno value from notify return value. */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int notifier_to_errno(int ret)
{
	ret &= ~0x8000;
	return ret > 0x0001 ? 0x0001 - ret : 0;
}

/*
 *	Declared notifiers so far. I can imagine quite a few more chains
 *	over time (eg laptop power reset chains, reboot chain (to clean 
 *	device units up), device [un]mount chain, module load/unload chain,
 *	low memory chain, screenblank chain (for plug in modular screenblankers) 
 *	VC switch chains (for loadable kernel svgalib VC switch helpers) etc...
 */
 
/* netdevice notifier chain */



































/* Used for CPU hotplug events occuring while tasks are frozen due to a suspend
 * operation in progress
 */











/* Hibernation and suspend events */







/* Console keyboard events.
 * Note: KBD_KEYCODE is always sent before KBD_UNBOUND_KEYCODE, KBD_UNICODE and
 * KBD_KEYSYM. */






extern struct blocking_notifier_head reboot_notifier_list;

/* Virtual Terminal events. */








#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/memory_hotplug.h" 2

struct page;
struct zone;
struct pglist_data;
struct mem_section;

#if definedEx(CONFIG_MEMORY_HOTPLUG)
/*
 * Types for free bootmem.
 * The normal smallest mapcount is -1. Here is smaller value than it.
 */




/*
 * pgdat resizing functions
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif

void pgdat_resize_lock(struct pglist_data *pgdat, unsigned long *flags)
{
	do { 
#if ((definedEx(CONFIG_DEBUG_SPINLOCK) || definedEx(CONFIG_SMP)) && (definedEx(CONFIG_SMP) || definedEx(CONFIG_DEBUG_SPINLOCK)))
do { ({ unsigned long __dummy; typeof(*flags) __dummy2; (void)(&__dummy == &__dummy2); 1; }); *flags = 
#if ((definedEx(CONFIG_DEBUG_SPINLOCK) || definedEx(CONFIG_SMP)) && definedEx(CONFIG_INLINE_SPIN_LOCK_IRQSAVE) && (definedEx(CONFIG_DEBUG_SPINLOCK) || definedEx(CONFIG_SMP)))
__raw_spin_lock_irqsave(spinlock_check(&pgdat->node_size_lock))
#endif
#if !(((definedEx(CONFIG_SMP) || definedEx(CONFIG_DEBUG_SPINLOCK)) && definedEx(CONFIG_INLINE_SPIN_LOCK_IRQSAVE) && (definedEx(CONFIG_SMP) || definedEx(CONFIG_DEBUG_SPINLOCK))))
_raw_spin_lock_irqsave(spinlock_check(&pgdat->node_size_lock))
#endif
; } while (0)
#endif
#if (!((definedEx(CONFIG_SMP) || definedEx(CONFIG_DEBUG_SPINLOCK))) && !(((definedEx(CONFIG_DEBUG_SPINLOCK) || definedEx(CONFIG_SMP)) && (definedEx(CONFIG_SMP) || definedEx(CONFIG_DEBUG_SPINLOCK)))))
do { ({ unsigned long __dummy; typeof(*flags) __dummy2; (void)(&__dummy == &__dummy2); 1; }); do { do { ({ unsigned long __dummy; typeof(*flags) __dummy2; (void)(&__dummy == &__dummy2); 1; }); do { (*flags) = __raw_local_irq_save(); } while (0); 
#if !(definedEx(CONFIG_TRACE_IRQFLAGS))
do { } while (0)
#endif
#if definedEx(CONFIG_TRACE_IRQFLAGS)
trace_hardirqs_off()
#endif
; } while (0); do { 
#if (definedEx(CONFIG_PREEMPT) && definedEx(CONFIG_PREEMPT))
do { 
#if !((definedEx(CONFIG_PREEMPT_TRACER) || definedEx(CONFIG_DEBUG_PREEMPT)))
do { (current_thread_info()->preempt_count) += (1); } while (0)
#endif
#if (definedEx(CONFIG_DEBUG_PREEMPT) || definedEx(CONFIG_PREEMPT_TRACER))
add_preempt_count(1)
#endif
; __asm__ __volatile__("": : :"memory"); } while (0)
#endif
#if (!(definedEx(CONFIG_PREEMPT)) && !((definedEx(CONFIG_PREEMPT) && definedEx(CONFIG_PREEMPT))))
do { } while (0)
#endif
; (void)0; (void)(spinlock_check(&pgdat->node_size_lock)); } while (0); } while (0); } while (0)
#endif
; } while (0);
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif

void pgdat_resize_unlock(struct pglist_data *pgdat, unsigned long *flags)
{
	spin_unlock_irqrestore(&pgdat->node_size_lock, *flags);
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif

void pgdat_resize_init(struct pglist_data *pgdat)
{
	do { spinlock_check(&pgdat->node_size_lock); 
#if (definedEx(CONFIG_DEBUG_SPINLOCK) && definedEx(CONFIG_DEBUG_SPINLOCK))
do { static struct lock_class_key __key; __raw_spin_lock_init((&(&pgdat->node_size_lock)->rlock), "&(&pgdat->node_size_lock)->rlock", &__key); } while (0)
#endif
#if (!(definedEx(CONFIG_DEBUG_SPINLOCK)) && !((definedEx(CONFIG_DEBUG_SPINLOCK) && definedEx(CONFIG_DEBUG_SPINLOCK))))
do { *(&(&pgdat->node_size_lock)->rlock) = (raw_spinlock_t) { .raw_lock = 
#if (definedEx(CONFIG_SMP) && !((!(definedEx(CONFIG_SMP)) && definedEx(CONFIG_DEBUG_SPINLOCK))) && !((!(definedEx(CONFIG_SMP)) && !(definedEx(CONFIG_DEBUG_SPINLOCK)))))
{ 0 }
#endif
#if (!(definedEx(CONFIG_SMP)) && !(definedEx(CONFIG_DEBUG_SPINLOCK)) && !((definedEx(CONFIG_SMP) && !((!(definedEx(CONFIG_SMP)) && definedEx(CONFIG_DEBUG_SPINLOCK))) && !((!(definedEx(CONFIG_SMP)) && !(definedEx(CONFIG_DEBUG_SPINLOCK)))))))
{ }
#endif
,  
#if (definedEx(CONFIG_DEBUG_LOCK_ALLOC) && definedEx(CONFIG_DEBUG_LOCK_ALLOC))
.dep_map = { .name = "&(&pgdat->node_size_lock)->rlock" }
#endif
#if (!(definedEx(CONFIG_DEBUG_LOCK_ALLOC)) && !((definedEx(CONFIG_DEBUG_LOCK_ALLOC) && definedEx(CONFIG_DEBUG_LOCK_ALLOC))))

#endif
 }; } while (0)
#endif
; } while (0);
}
/*
 * Zone resizing functions
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned zone_span_seqbegin(struct zone *zone)
{
	return read_seqbegin(&zone->span_seqlock);
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int zone_span_seqretry(struct zone *zone, unsigned iv)
{
	return read_seqretry(&zone->span_seqlock, iv);
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void zone_span_writelock(struct zone *zone)
{
	write_seqlock(&zone->span_seqlock);
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void zone_span_writeunlock(struct zone *zone)
{
	write_sequnlock(&zone->span_seqlock);
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void zone_seqlock_init(struct zone *zone)
{
	do { (&zone->span_seqlock)->sequence = 0; do { spinlock_check(&(&zone->span_seqlock)->lock); 
#if (definedEx(CONFIG_DEBUG_SPINLOCK) && definedEx(CONFIG_DEBUG_SPINLOCK))
do { static struct lock_class_key __key; __raw_spin_lock_init((&(&(&zone->span_seqlock)->lock)->rlock), "&(&(&zone->span_seqlock)->lock)->rlock", &__key); } while (0)
#endif
#if (!(definedEx(CONFIG_DEBUG_SPINLOCK)) && !((definedEx(CONFIG_DEBUG_SPINLOCK) && definedEx(CONFIG_DEBUG_SPINLOCK))))
do { *(&(&(&zone->span_seqlock)->lock)->rlock) = (raw_spinlock_t) { .raw_lock = 
#if (definedEx(CONFIG_SMP) && !((!(definedEx(CONFIG_SMP)) && definedEx(CONFIG_DEBUG_SPINLOCK))) && !((!(definedEx(CONFIG_SMP)) && !(definedEx(CONFIG_DEBUG_SPINLOCK)))))
{ 0 }
#endif
#if (!(definedEx(CONFIG_SMP)) && !(definedEx(CONFIG_DEBUG_SPINLOCK)) && !((definedEx(CONFIG_SMP) && !((!(definedEx(CONFIG_SMP)) && definedEx(CONFIG_DEBUG_SPINLOCK))) && !((!(definedEx(CONFIG_SMP)) && !(definedEx(CONFIG_DEBUG_SPINLOCK)))))))
{ }
#endif
,  
#if (definedEx(CONFIG_DEBUG_LOCK_ALLOC) && definedEx(CONFIG_DEBUG_LOCK_ALLOC))
.dep_map = { .name = "&(&(&zone->span_seqlock)->lock)->rlock" }
#endif
#if (!(definedEx(CONFIG_DEBUG_LOCK_ALLOC)) && !((definedEx(CONFIG_DEBUG_LOCK_ALLOC) && definedEx(CONFIG_DEBUG_LOCK_ALLOC))))

#endif
 }; } while (0)
#endif
; } while (0); } while (0);
}
extern int zone_grow_free_lists(struct zone *zone, unsigned long new_nr_pages);
extern int zone_grow_waitqueues(struct zone *zone, unsigned long nr_pages);
extern int add_one_highpage(struct page *page, int pfn, int bad_ppro);
/* need some defines for these for archs that don't support it */
extern void online_page(struct page *page);
/* VM interface that may be used by firmware interface */
extern int online_pages(unsigned long, unsigned long);
extern void __offline_isolated_pages(unsigned long, unsigned long);

/* reasonably generic interface to expand the physical pages in a zone  */
extern int __add_pages(int nid, struct zone *zone, unsigned long start_pfn,
	unsigned long nr_pages);
extern int __remove_pages(struct zone *zone, unsigned long start_pfn,
	unsigned long nr_pages);

#if definedEx(CONFIG_NUMA)
extern int memory_add_physaddr_to_nid(u64 start);
#endif
#if !(definedEx(CONFIG_NUMA))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int memory_add_physaddr_to_nid(u64 start)
{
	return 0;
}
#endif
#if definedEx(CONFIG_HAVE_ARCH_NODEDATA_EXTENSION)
/*
 * For supporting node-hotadd, we have to allocate a new pgdat.
 *
 * If an arch has generic style NODE_DATA(),
 * node_data[nid] = kzalloc() works well. But it depends on the architecture.
 *
 * In general, generic_alloc_nodedata() is used.
 * Now, arch_free_nodedata() is just defined for error path of node_hot_add.
 *
 */
extern pg_data_t *arch_alloc_nodedata(int nid);
extern void arch_free_nodedata(pg_data_t *pgdat);
extern void arch_refresh_nodedata(int nid, pg_data_t *pgdat);

#endif
#if !(definedEx(CONFIG_HAVE_ARCH_NODEDATA_EXTENSION))



#if definedEx(CONFIG_NUMA)
/*
 * If ARCH_HAS_NODEDATA_EXTENSION=n, this func is used to allocate pgdat.
 * XXX: kmalloc_node() can't work well to get new node's memory at this time.
 *	Because, pgdat for the new node is not allocated/initialized yet itself.
 *	To use new node's memory, more consideration will be necessary.
 */




/*
 * This definition is just for error path in node hotadd.
 * For node hotremove, we have to replace this.
 */


extern pg_data_t *node_data[];
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void arch_refresh_nodedata(int nid, pg_data_t *pgdat)
{
	node_data[nid] = pgdat;
}

#endif
#if !(definedEx(CONFIG_NUMA))
/* never called */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 pg_data_t *generic_alloc_nodedata(int nid)
{
	do { asm volatile("1:\tud2\n" ".pushsection __bug_table,\"a\"\n" "2:\t.long 1b, %c0\n" "\t.word %c1, 0\n" "\t.org 2b+%c2\n" ".popsection" : : "i" ("/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/memory_hotplug.h"), "i" (15), "i" (sizeof(struct bug_entry))); do { } while (1); } while (0);
	return ((void *)0);
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void generic_free_nodedata(pg_data_t *pgdat)
{
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void arch_refresh_nodedata(int nid, pg_data_t *pgdat)
{
}
#endif
#endif
#if definedEx(CONFIG_SPARSEMEM_VMEMMAP)
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void register_page_bootmem_info_node(struct pglist_data *pgdat)
{
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void put_page_bootmem(struct page *page)
{
}
#endif
#if !(definedEx(CONFIG_SPARSEMEM_VMEMMAP))
extern void register_page_bootmem_info_node(struct pglist_data *pgdat);
extern void put_page_bootmem(struct page *page);
#endif
#endif
#if !(definedEx(CONFIG_MEMORY_HOTPLUG))
/*
 * Stub functions for when hotplug is off
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void pgdat_resize_lock(struct pglist_data *p, unsigned long *f) {}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void pgdat_resize_unlock(struct pglist_data *p, unsigned long *f) {}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void pgdat_resize_init(struct pglist_data *pgdat) {}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned zone_span_seqbegin(struct zone *zone)
{
	return 0;
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int zone_span_seqretry(struct zone *zone, unsigned iv)
{
	return 0;
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void zone_span_writelock(struct zone *zone) {}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void zone_span_writeunlock(struct zone *zone) {}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void zone_seqlock_init(struct zone *zone) {}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int mhp_notimplemented(const char *func)
{
	printk("<4>" "%s() called, with CONFIG_MEMORY_HOTPLUG disabled\n", func);
	dump_stack();
	return -38;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void register_page_bootmem_info_node(struct pglist_data *pgdat)
{
}

#endif
#if definedEx(CONFIG_MEMORY_HOTREMOVE)
extern int is_mem_section_removable(unsigned long pfn, unsigned long nr_pages);

#endif
#if !(definedEx(CONFIG_MEMORY_HOTREMOVE))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int is_mem_section_removable(unsigned long pfn,
					unsigned long nr_pages)
{
	return 0;
}
#endif
extern int add_memory(int nid, u64 start, u64 size);
extern int arch_add_memory(int nid, u64 start, u64 size);
extern int remove_memory(u64 start, u64 size);
extern int sparse_add_one_section(struct zone *zone, unsigned long start_pfn,
								int nr_pages);
extern void sparse_remove_one_section(struct zone *zone, struct mem_section *ms);
extern struct page *sparse_decode_mem_map(unsigned long coded_mem_map,
					  unsigned long pnum);


#line 655 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mmzone.h" 2

void get_zone_counts(unsigned long *active, unsigned long *inactive,
			unsigned long *free);
void build_all_zonelists(void);
void wakeup_kswapd(struct zone *zone, int order);
int zone_watermark_ok(struct zone *z, int order, unsigned long mark,
		int classzone_idx, int alloc_flags);
enum memmap_context {
	MEMMAP_EARLY,
	MEMMAP_HOTPLUG,
};
extern int init_currently_empty_zone(struct zone *zone, unsigned long start_pfn,
				     unsigned long size,
				     enum memmap_context context);

#if definedEx(CONFIG_HAVE_MEMORY_PRESENT)
void memory_present(int nid, unsigned long start, unsigned long end);
#endif
#if !(definedEx(CONFIG_HAVE_MEMORY_PRESENT))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void memory_present(int nid, unsigned long start, unsigned long end) {}
#endif
#if definedEx(CONFIG_NEED_NODE_MEMMAP_SIZE)
unsigned long __attribute__ ((__section__(".init.text")))  __attribute__((no_instrument_function)) node_memmap_size_bytes(int, unsigned long, unsigned long);
#endif
/*
 * zone_idx() returns 0 for the ZONE_DMA zone, 1 for the ZONE_NORMAL zone, etc.
 */


static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int populated_zone(struct zone *zone)
{
	return (!!zone->present_pages);
}

extern int movable_zone;

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int zone_movable_is_highmem(void)
{

	return movable_zone == ZONE_HIGHMEM;
 	

}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int is_highmem_idx(enum zone_type idx)
{

	return (idx == ZONE_HIGHMEM ||
		(idx == ZONE_MOVABLE && zone_movable_is_highmem()));
 	

}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int is_normal_idx(enum zone_type idx)
{
	return (idx == ZONE_NORMAL);
}

/**
 * is_highmem - helper function to quickly check if a struct zone is a 
 *              highmem zone or not.  This is an attempt to keep references
 *              to ZONE_{DMA/NORMAL/HIGHMEM/etc} in general code to a minimum.
 * @zone - pointer to struct zone variable
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int is_highmem(struct zone *zone)
{

	int zone_off = (char *)zone - (char *)zone->zone_pgdat->node_zones;
	return zone_off == ZONE_HIGHMEM * sizeof(*zone) ||
	       (zone_off == ZONE_MOVABLE * sizeof(*zone) &&
		zone_movable_is_highmem());
 	

}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int is_normal(struct zone *zone)
{
	return zone == zone->zone_pgdat->node_zones + ZONE_NORMAL;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int is_dma32(struct zone *zone)
{
#if definedEx(CONFIG_ZONE_DMA32)
	return zone == zone->zone_pgdat->node_zones + ZONE_DMA32;
#endif
#if !(definedEx(CONFIG_ZONE_DMA32))
	return 0;
#endif
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int is_dma(struct zone *zone)
{

	return zone == zone->zone_pgdat->node_zones + ZONE_DMA;
 	

}

/* These two functions are used to setup the per zone pages min values */
struct ctl_table;
int min_free_kbytes_sysctl_handler(struct ctl_table *, int,
					void  *, size_t *, loff_t *);
extern int sysctl_lowmem_reserve_ratio[4-1];
int lowmem_reserve_ratio_sysctl_handler(struct ctl_table *, int,
					void  *, size_t *, loff_t *);
int percpu_pagelist_fraction_sysctl_handler(struct ctl_table *, int,
					void  *, size_t *, loff_t *);
int sysctl_min_unmapped_ratio_sysctl_handler(struct ctl_table *, int,
			void  *, size_t *, loff_t *);
int sysctl_min_slab_ratio_sysctl_handler(struct ctl_table *, int,
			void  *, size_t *, loff_t *);

extern int numa_zonelist_order_handler(struct ctl_table *, int,
			void  *, size_t *, loff_t *);
extern char numa_zonelist_order[];


#if !(definedEx(CONFIG_NEED_MULTIPLE_NODES))
extern struct pglist_data contig_page_data;



#endif
#if definedEx(CONFIG_NEED_MULTIPLE_NODES)
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mmzone.h" 1

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mmzone_32.h" 1
/*
 * Written by Pat Gaughen (gone@us.ibm.com) Mar 2002
 *
 */




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/smp.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/cpumask.h" 1

























 





 























































 












	

	






	





	




	



					    
					    

	




					   

	






 







	











	
	
		
	











	
	
		
	





















































	









	





















	











	








	








	









			       
			       

	
				       









			      

	
				      









			       
			       

	
				       









				  
				  

	
					  








				      

	
					      








				

	
						 








				     

	
						      








				 

	
						  








	








	








	









				       

	
					       









				      

	
					      








				

	












































				    

	











				     

	












				    

	
				    












	









	

	





























 



	



					  

	




	
	



					  

	
	



















































	










	



	
	
	










 



























 






















 














 











	





	





	





	








	




					

	




					

	




					

	





					

	




					

	




					

	




					

	





	





	





					

	



#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/smp.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/init.h" 1






































































 

















































































  





























































	
	
	


















































 






































 














 



 


#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/smp.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/percpu.h" 1






 




















 





 

 





 













 















































































































































































































 














#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/smp.h" 2

/*
 * We need the APIC definitions automatically as part of 'smp.h'
 */
#if definedEx(CONFIG_X86_LOCAL_APIC)
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mpspec.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/init.h" 1






































































 

















































































  





























































	
	
	


















































 






































 














 



 


#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mpspec.h" 2

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mpspec_def.h" 1



/*
 * Structure definitions for SMP machines following the
 * Intel Multiprocessing Specification 1.1 and 1.4.
 */

/*
 * This tag identifies where the SMP configuration
 * information is.
 */






 

 


/* Intel MP Floating Pointer Structure */
struct mpf_intel {
	char signature[4];		/* "_MP_"			*/
	unsigned int physptr;		/* Configuration table address	*/
	unsigned char length;		/* Our length (paragraphs)	*/
	unsigned char specification;	/* Specification version	*/
	unsigned char checksum;		/* Checksum (makes sum 0)	*/
	unsigned char feature1;		/* Standard or configuration ?	*/
	unsigned char feature2;		/* Bit7 set for IMCR|PIC	*/
	unsigned char feature3;		/* Unused (0)			*/
	unsigned char feature4;		/* Unused (0)			*/
	unsigned char feature5;		/* Unused (0)			*/
};



struct mpc_table {
	char signature[4];
	unsigned short length;		/* Size of table */
	char spec;			/* 0x01 */
	char checksum;
	char oem[8];
	char productid[12];
	unsigned int oemptr;		/* 0 if not present */
	unsigned short oemsize;		/* 0 if not present */
	unsigned short oemcount;
	unsigned int lapic;		/* APIC address */
	unsigned int reserved;
};

/* Followed by entries */






/* Used by IBM NUMA-Q to describe node locality */









struct mpc_cpu {
	unsigned char type;
	unsigned char apicid;		/* Local APIC number */
	unsigned char apicver;		/* Its versions */
	unsigned char cpuflag;
	unsigned int cpufeature;
	unsigned int featureflag;	/* CPUID feature value */
	unsigned int reserved[2];
};

struct mpc_bus {
	unsigned char type;
	unsigned char busid;
	unsigned char bustype[6];
};

/* List of Bus Type string values, Intel MP Spec. */





















struct mpc_ioapic {
	unsigned char type;
	unsigned char apicid;
	unsigned char apicver;
	unsigned char flags;
	unsigned int apicaddr;
};

struct mpc_intsrc {
	unsigned char type;
	unsigned char irqtype;
	unsigned short irqflag;
	unsigned char srcbus;
	unsigned char srcbusirq;
	unsigned char dstapic;
	unsigned char dstirq;
};

enum mp_irq_source_types {
	mp_INT = 0,
	mp_NMI = 1,
	mp_SMI = 2,
	mp_ExtINT = 3
};







struct mpc_lintsrc {
	unsigned char type;
	unsigned char irqtype;
	unsigned short irqflag;
	unsigned char srcbusid;
	unsigned char srcbusirq;
	unsigned char destapic;
	unsigned char destapiclint;
};



struct mpc_oemtable {
	char signature[4];
	unsigned short length;		/* Size of table */
	char  rev;			/* 0x01 */
	char  checksum;
	char  mpc[8];
};

/*
 *	Default configurations
 *
 *	1	2 CPU ISA 82489DX
 *	2	2 CPU EISA 82489DX neither IRQ 0 timer nor IRQ 13 DMA chaining
 *	3	2 CPU EISA 82489DX
 *	4	2 CPU MCA 82489DX
 *	5	2 CPU ISA+PCI
 *	6	2 CPU EISA+PCI
 *	7	2 CPU MCA+PCI
 */

enum mp_bustype {
	MP_BUS_ISA = 1,
	MP_BUS_EISA,
	MP_BUS_PCI,
	MP_BUS_MCA,
};

#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mpspec.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/x86_init.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/pgtable_types.h" 1



















































 



 



































































































 










 
















	




	




	







	




	

 



	







	




	

 



	




	




	




	




	




	





















                              

                              







 






	
	
	
	
	




 











#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/x86_init.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bootparam.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bootparam.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/screen_info.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/screen_info.h" 2

/*
 * These are set up by the setup-routine at boot-time:
 */

struct screen_info {
	__u8  orig_x;		/* 0x00 */
	__u8  orig_y;		/* 0x01 */
	__u16 ext_mem_k;	/* 0x02 */
	__u16 orig_video_page;	/* 0x04 */
	__u8  orig_video_mode;	/* 0x06 */
	__u8  orig_video_cols;	/* 0x07 */
	__u8  flags;		/* 0x08 */
	__u8  unused2;		/* 0x09 */
	__u16 orig_video_ega_bx;/* 0x0a */
	__u16 unused3;		/* 0x0c */
	__u8  orig_video_lines;	/* 0x0e */
	__u8  orig_video_isVGA;	/* 0x0f */
	__u16 orig_video_points;/* 0x10 */

	/* VESA graphic mode -- linear frame buffer */
	__u16 lfb_width;	/* 0x12 */
	__u16 lfb_height;	/* 0x14 */
	__u16 lfb_depth;	/* 0x16 */
	__u32 lfb_base;		/* 0x18 */
	__u32 lfb_size;		/* 0x1c */
	__u16 cl_magic, cl_offset; /* 0x20 */
	__u16 lfb_linelength;	/* 0x24 */
	__u8  red_size;		/* 0x26 */
	__u8  red_pos;		/* 0x27 */
	__u8  green_size;	/* 0x28 */
	__u8  green_pos;	/* 0x29 */
	__u8  blue_size;	/* 0x2a */
	__u8  blue_pos;		/* 0x2b */
	__u8  rsvd_size;	/* 0x2c */
	__u8  rsvd_pos;		/* 0x2d */
	__u16 vesapm_seg;	/* 0x2e */
	__u16 vesapm_off;	/* 0x30 */
	__u16 pages;		/* 0x32 */
	__u16 vesa_attributes;	/* 0x34 */
	__u32 capabilities;     /* 0x36 */
	__u8  _reserved[6];	/* 0x3a */
} __attribute__((packed));
























extern struct screen_info screen_info;











#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bootparam.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/apm_bios.h" 1



/*
 * Include file for the interface to an APM BIOS
 * Copyright 1994-2001 Stephen Rothwell (sfr@canb.auug.org.au)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 */

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 21 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/apm_bios.h" 2

typedef unsigned short	apm_event_t;
typedef unsigned short	apm_eventinfo_t;

struct apm_bios_info {
	__u16	version;
	__u16	cseg;
	__u32	offset;
	__u16	cseg_16;
	__u16	dseg;
	__u16	flags;
	__u16	cseg_len;
	__u16	cseg_16_len;
	__u16	dseg_len;
};






/* Results of APM Installation Check */






/*
 * Data for APM that is persistent across module unload/load
 */
struct apm_info {
	struct apm_bios_info	bios;
	unsigned short		connection_version;
	int			get_power_status_broken;
	int			get_power_status_swabinminutes;
	int			allow_ints;
	int			forbid_idle;
	int			realmode_power_off;
	int			disabled;
};

/*
 * The APM function codes
 */





















/*
 * Function code for APM_FUNC_RESUME_TIMER
 */




/*
 * Function code for APM_FUNC_RESUME_ON_RING
 */




/*
 * Function code for APM_FUNC_TIMER_STATUS
 */




/*
 * in arch/i386/kernel/setup.c
 */
extern struct apm_info	apm_info;


/*
 * Power states
 */















/*
 * Events (results of Get PM Event)
 */













/*
 * Error codes
 */


















/*
 * APM Device IDs
 */















/*
 * This is the "All Devices" ID communicated to the BIOS
 */



/*
 * Battery status
 */


/*
 * APM defined capability bit flags
 */









/*
 * ioctl operations
 */
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/ioctl.h" 1






#line 217 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/apm_bios.h" 2





#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bootparam.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/edd.h" 1
/*
 * linux/include/linux/edd.h
 *  Copyright (C) 2002, 2003, 2004 Dell Inc.
 *  by Matt Domsch <Matt_Domsch@dell.com>
 *
 * structures and definitions for the int 13h, ax={41,48}h
 * BIOS Enhanced Disk Drive Services
 * This is based on the T13 group document D1572 Revision 0 (August 14 2002)
 * available at http://www.t13.org/docs2002/d1572r0.pdf.  It is
 * very similar to D1484 Revision 3 http://www.t13.org/docs2002/d1484r3.pdf
 *
 * In a nutshell, arch/{i386,x86_64}/boot/setup.S populates a scratch
 * table in the boot_params that contains a list of BIOS-enumerated
 * boot devices.
 * In arch/{i386,x86_64}/kernel/setup.c, this information is
 * transferred into the edd structure, and in drivers/firmware/edd.c, that
 * information is used to identify BIOS boot disk.  The code in setup.S
 * is very sensitive to the size of these structures.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v2.0 as published by
 * the Free Software Foundation
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 */



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 35 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/edd.h" 2


































struct edd_device_params {
	__u16 length;
	__u16 info_flags;
	__u32 num_default_cylinders;
	__u32 num_default_heads;
	__u32 sectors_per_track;
	__u64 number_of_sectors;
	__u16 bytes_per_sector;
	__u32 dpte_ptr;		/* 0xFFFFFFFF for our purposes */
	__u16 key;		/* = 0xBEDD */
	__u8 device_path_info_length;	/* = 44 */
	__u8 reserved2;
	__u16 reserved3;
	__u8 host_bus_type[4];
	__u8 interface_type[8];
	union {
		struct {
			__u16 base_address;
			__u16 reserved1;
			__u32 reserved2;
		} __attribute__ ((packed)) isa;
		struct {
			__u8 bus;
			__u8 slot;
			__u8 function;
			__u8 channel;
			__u32 reserved;
		} __attribute__ ((packed)) pci;
		/* pcix is same as pci */
		struct {
			__u64 reserved;
		} __attribute__ ((packed)) ibnd;
		struct {
			__u64 reserved;
		} __attribute__ ((packed)) xprs;
		struct {
			__u64 reserved;
		} __attribute__ ((packed)) htpt;
		struct {
			__u64 reserved;
		} __attribute__ ((packed)) unknown;
	} interface_path;
	union {
		struct {
			__u8 device;
			__u8 reserved1;
			__u16 reserved2;
			__u32 reserved3;
			__u64 reserved4;
		} __attribute__ ((packed)) ata;
		struct {
			__u8 device;
			__u8 lun;
			__u8 reserved1;
			__u8 reserved2;
			__u32 reserved3;
			__u64 reserved4;
		} __attribute__ ((packed)) atapi;
		struct {
			__u16 id;
			__u64 lun;
			__u16 reserved1;
			__u32 reserved2;
		} __attribute__ ((packed)) scsi;
		struct {
			__u64 serial_number;
			__u64 reserved;
		} __attribute__ ((packed)) usb;
		struct {
			__u64 eui;
			__u64 reserved;
		} __attribute__ ((packed)) i1394;
		struct {
			__u64 wwid;
			__u64 lun;
		} __attribute__ ((packed)) fibre;
		struct {
			__u64 identity_tag;
			__u64 reserved;
		} __attribute__ ((packed)) i2o;
		struct {
			__u32 array_number;
			__u32 reserved1;
			__u64 reserved2;
		} __attribute__ ((packed)) raid;
		struct {
			__u8 device;
			__u8 reserved1;
			__u16 reserved2;
			__u32 reserved3;
			__u64 reserved4;
		} __attribute__ ((packed)) sata;
		struct {
			__u64 reserved1;
			__u64 reserved2;
		} __attribute__ ((packed)) unknown;
	} device_path;
	__u8 reserved4;
	__u8 checksum;
} __attribute__ ((packed));

struct edd_info {
	__u8 device;
	__u8 version;
	__u16 interface_support;
	__u16 legacy_max_cylinder;
	__u8 legacy_max_head;
	__u8 legacy_sectors_per_track;
	struct edd_device_params params;
} __attribute__ ((packed));

struct edd {
	unsigned int mbr_signature[16];
	struct edd_info edd_info[6];
	unsigned char mbr_signature_nr;
	unsigned char edd_info_nr;
};


extern struct edd edd;



#line 9 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bootparam.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/e820.h" 1





/*
 * Legacy E820 BIOS limits us to 128 (E820MAX) nodes due to the
 * constrained space in the zeropage.  If we have more nodes than
 * that, and if we've booted off EFI firmware, then the EFI tables
 * passed us from the EFI firmware can list more nodes.  Size our
 * internal memory map tables to have room for these additional
 * nodes, based on up to three entries per node for which the
 * kernel was built: MAX_NUMNODES == (1 << CONFIG_NODES_SHIFT),
 * plus E820MAX, allowing space for the possible duplicate E820
 * entries that might need room in the same arrays, prior to the
 * call to sanitize_e820_map() to remove duplicates.  The allowance
 * of three memory map entries per node is "enough" entries for
 * the initial hardware platform motivating this mechanism to make
 * use of additional EFI map entries.  Future platforms may want
 * to allow more than three entries per node or otherwise refine
 * this size.
 */

/*
 * Odd: 'make headers_check' complains about numa.h if I try
 * to collapse the next two #ifdef lines to a single line:
 *	#if defined(__KERNEL__) && defined(CONFIG_EFI)
 */

#if definedEx(CONFIG_EFI)
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/numa.h" 1






 






#line 33 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/e820.h" 2

#endif
#if !(definedEx(CONFIG_EFI))

#endif
 









/* reserved RAM used by kernel itself */



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 54 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/e820.h" 2
struct e820entry {
	__u64 addr;	/* start of memory segment */
	__u64 size;	/* size of memory segment */
	__u32 type;	/* type of memory segment */
} __attribute__((packed));

struct e820map {
	__u32 nr_map;
	struct e820entry map[
#if (definedEx(CONFIG_EFI) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC) && !((!(definedEx(CONFIG_EFI)) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))))
(128 + 3 * (1 << 
#if (definedEx(CONFIG_NODES_SHIFT) && definedEx(CONFIG_NODES_SHIFT))
CONFIG_NODES_SHIFT
#endif
#if (!(definedEx(CONFIG_NODES_SHIFT)) && !((definedEx(CONFIG_NODES_SHIFT) && definedEx(CONFIG_NODES_SHIFT))))
0
#endif
))
#endif
#if (!(definedEx(CONFIG_EFI)) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC) && !((definedEx(CONFIG_EFI) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC) && !((!(definedEx(CONFIG_EFI)) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))))))
128
#endif
];
};








/* see comment in arch/x86/kernel/e820.c */
extern struct e820map e820;
extern struct e820map e820_saved;

extern unsigned long pci_mem_start;
extern int e820_any_mapped(u64 start, u64 end, unsigned type);
extern int e820_all_mapped(u64 start, u64 end, unsigned type);
extern void e820_add_region(u64 start, u64 size, int type);
extern void e820_print_map(char *who);
extern int
sanitize_e820_map(struct e820entry *biosmap, int max_nr_map, u32 *pnr_map);
extern u64 e820_update_range(u64 start, u64 size, unsigned old_type,
			       unsigned new_type);
extern u64 e820_remove_range(u64 start, u64 size, unsigned old_type,
			     int checktype);
extern void update_e820(void);
extern void e820_setup_gap(void);
extern int e820_search_gap(unsigned long *gapstart, unsigned long *gapsize,
			unsigned long start_addr, unsigned long long end_addr);
struct setup_data;
extern void parse_e820_ext(struct setup_data *data, unsigned long pa_data);

#if (definedEx(CONFIG_X86_64) || definedEx(CONFIG_HIBERNATION))
extern void e820_mark_nosave_regions(unsigned long limit_pfn);
#endif
#if !((definedEx(CONFIG_X86_64) || definedEx(CONFIG_HIBERNATION)))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void e820_mark_nosave_regions(unsigned long limit_pfn)
{
}
#endif
#if definedEx(CONFIG_MEMTEST)
extern void early_memtest(unsigned long start, unsigned long end);
#endif
#if !(definedEx(CONFIG_MEMTEST))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void early_memtest(unsigned long start, unsigned long end)
{
}
#endif
extern unsigned long end_user_pfn;

extern u64 find_e820_area(u64 start, u64 end, u64 size, u64 align);
extern u64 find_e820_area_size(u64 start, u64 *sizep, u64 align);
extern void reserve_early(u64 start, u64 end, char *name);
extern void reserve_early_overlap_ok(u64 start, u64 end, char *name);
extern void free_early(u64 start, u64 end);
extern void early_res_to_bootmem(u64 start, u64 end);
extern u64 early_reserve_e820(u64 startt, u64 sizet, u64 align);

extern unsigned long e820_end_of_ram_pfn(void);
extern unsigned long e820_end_of_low_ram_pfn(void);
extern int e820_find_active_region(const struct e820entry *ei,
				  unsigned long start_pfn,
				  unsigned long last_pfn,
				  unsigned long *ei_startpfn,
				  unsigned long *ei_endpfn);
extern void e820_register_active_regions(int nid, unsigned long start_pfn,
					 unsigned long end_pfn);
extern u64 e820_hole_size(u64 start, u64 end);
extern void finish_e820_parsing(void);
extern void e820_reserve_resources(void);
extern void e820_reserve_resources_late(void);
extern void setup_memory_map(void);
extern char *default_machine_specific_memory_setup(void);

/*
 * Returns true iff the specified range [s,e) is completely contained inside
 * the ISA region.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 bool is_ISA_range(u64 s, u64 e)
{
	return s >= 0xa0000 && e <= 0x100000;
}




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/ioport.h" 1
/*
 * ioport.h	Definitions of routines for detecting, reserving and
 *		allocating system resources.
 *
 * Authors:	Linus Torvalds
 */





#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 14 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/ioport.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 15 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/ioport.h" 2
/*
 * Resources are tree-like, allowing
 * nesting etc..
 */
struct resource {
	resource_size_t start;
	resource_size_t end;
	const char *name;
	unsigned long flags;
	struct resource *parent, *sibling, *child;
};

struct resource_list {
	struct resource_list *next;
	struct resource *res;
	struct pci_dev *dev;
};

/*
 * IO resources have these defined flags.
 */

























/* PnP IRQ specific bits (IORESOURCE_BITS) */







/* PnP DMA specific bits (IORESOURCE_BITS) */















/* PnP memory I/O specific bits (IORESOURCE_BITS) */











/* PnP I/O specific bits (IORESOURCE_BITS) */



/* PCI ROM control bits (IORESOURCE_BITS) */





/* PCI control bits.  Shares IORESOURCE_BITS with above PCI ROM.  */


/* PC/ISA/whatever - the normal PC address spaces: IO and memory */
extern struct resource ioport_resource;
extern struct resource iomem_resource;

extern int request_resource(struct resource *root, struct resource *new);
extern int release_resource(struct resource *new);
extern void reserve_region_with_split(struct resource *root,
			     resource_size_t start, resource_size_t end,
			     const char *name);
extern int insert_resource(struct resource *parent, struct resource *new);
extern void insert_resource_expand_to_fit(struct resource *root, struct resource *new);
extern int allocate_resource(struct resource *root, struct resource *new,
			     resource_size_t size, resource_size_t min,
			     resource_size_t max, resource_size_t align,
			     void (*alignf)(void *, struct resource *,
					    resource_size_t, resource_size_t),
			     void *alignf_data);
int adjust_resource(struct resource *res, resource_size_t start,
		    resource_size_t size);
resource_size_t resource_alignment(struct resource *res);
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 resource_size_t resource_size(const struct resource *res)
{
	return res->end - res->start + 1;
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long resource_type(const struct resource *res)
{
	return res->flags & 0x00000f00;
}

/* Convenience shorthand with allocation */







extern struct resource * __request_region(struct resource *,
					resource_size_t start,
					resource_size_t n,
					const char *name, int flags);

/* Compatibility cruft */




extern int __check_region(struct resource *, resource_size_t, resource_size_t);
extern void __release_region(struct resource *, resource_size_t,
				resource_size_t);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int 
#if (definedEx(CONFIG_ENABLE_WARN_DEPRECATED) && definedEx(CONFIG_ENABLE_WARN_DEPRECATED))
__attribute__((deprecated))
#endif
#if (!(definedEx(CONFIG_ENABLE_WARN_DEPRECATED)) && !((definedEx(CONFIG_ENABLE_WARN_DEPRECATED) && definedEx(CONFIG_ENABLE_WARN_DEPRECATED))))

#endif
 check_region(resource_size_t s,
						resource_size_t n)
{
	return __check_region(&ioport_resource, s, n);
}

/* Wrappers for managed devices */
struct device;





extern struct resource * __devm_request_region(struct device *dev,
				struct resource *parent, resource_size_t start,
				resource_size_t n, const char *name);






extern void __devm_release_region(struct device *dev, struct resource *parent,
				  resource_size_t start, resource_size_t n);
extern int iomem_map_sanity_check(resource_size_t addr, unsigned long size);
extern int iomem_is_exclusive(u64 addr);

extern int
walk_system_ram_range(unsigned long start_pfn, unsigned long nr_pages,
		void *arg, int (*func)(unsigned long, unsigned long, void *));



#line 151 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/e820.h" 2




#line 10 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bootparam.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/ist.h" 1



/*
 * Include file for the interface to IST BIOS
 * Copyright 2002 Andy Grover <andrew.grover@intel.com>
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 */


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 22 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/ist.h" 2

struct ist_info {
	__u32 signature;
	__u32 command;
	__u32 event;
	__u32 perf_level;
};


extern struct ist_info ist_info;



#line 11 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bootparam.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/video/edid.h" 1




struct edid_info {
	unsigned char dummy[128];
};


extern struct edid_info edid_info;



#line 12 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/bootparam.h" 2

/* setup data types */



/* extensible setup data list node */
struct setup_data {
	__u64 next;
	__u32 type;
	__u32 len;
	__u8 data[0];
};

struct setup_header {
	__u8	setup_sects;
	__u16	root_flags;
	__u32	syssize;
	__u16	ram_size;



	__u16	vid_mode;
	__u16	root_dev;
	__u16	boot_flag;
	__u16	jump;
	__u32	header;
	__u16	version;
	__u32	realmode_swtch;
	__u16	start_sys;
	__u16	kernel_version;
	__u8	type_of_loader;
	__u8	loadflags;




	__u16	setup_move_size;
	__u32	code32_start;
	__u32	ramdisk_image;
	__u32	ramdisk_size;
	__u32	bootsect_kludge;
	__u16	heap_end_ptr;
	__u8	ext_loader_ver;
	__u8	ext_loader_type;
	__u32	cmd_line_ptr;
	__u32	initrd_addr_max;
	__u32	kernel_alignment;
	__u8	relocatable_kernel;
	__u8	_pad2[3];
	__u32	cmdline_size;
	__u32	hardware_subarch;
	__u64	hardware_subarch_data;
	__u32	payload_offset;
	__u32	payload_length;
	__u64	setup_data;
} __attribute__((packed));

struct sys_desc_table {
	__u16 length;
	__u8  table[14];
};

struct efi_info {
	__u32 efi_loader_signature;
	__u32 efi_systab;
	__u32 efi_memdesc_size;
	__u32 efi_memdesc_version;
	__u32 efi_memmap;
	__u32 efi_memmap_size;
	__u32 efi_systab_hi;
	__u32 efi_memmap_hi;
};

/* The so-called "zeropage" */
struct boot_params {
	struct screen_info screen_info;			/* 0x000 */
	struct apm_bios_info apm_bios_info;		/* 0x040 */
	__u8  _pad2[4];					/* 0x054 */
	__u64  tboot_addr;				/* 0x058 */
	struct ist_info ist_info;			/* 0x060 */
	__u8  _pad3[16];				/* 0x070 */
	__u8  hd0_info[16];	/* obsolete! */		/* 0x080 */
	__u8  hd1_info[16];	/* obsolete! */		/* 0x090 */
	struct sys_desc_table sys_desc_table;		/* 0x0a0 */
	__u8  _pad4[144];				/* 0x0b0 */
	struct edid_info edid_info;			/* 0x140 */
	struct efi_info efi_info;			/* 0x1c0 */
	__u32 alt_mem_k;				/* 0x1e0 */
	__u32 scratch;		/* Scratch field! */	/* 0x1e4 */
	__u8  e820_entries;				/* 0x1e8 */
	__u8  eddbuf_entries;				/* 0x1e9 */
	__u8  edd_mbr_sig_buf_entries;			/* 0x1ea */
	__u8  _pad6[6];					/* 0x1eb */
	struct setup_header hdr;    /* setup header */	/* 0x1f1 */
	__u8  _pad7[0x290-0x1f1-sizeof(struct setup_header)];
	__u32 edd_mbr_sig_buffer[16];	/* 0x290 */
	struct e820entry e820_map[128];		/* 0x2d0 */
	__u8  _pad8[48];				/* 0xcd0 */
	struct edd_info eddbuf[6];		/* 0xd00 */
	__u8  _pad9[276];				/* 0xeec */
} __attribute__((packed));

enum {
	X86_SUBARCH_PC = 0,
	X86_SUBARCH_LGUEST,
	X86_SUBARCH_XEN,
	X86_SUBARCH_MRST,
	X86_NR_SUBARCHS,
};




#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/x86_init.h" 2

struct mpc_bus;
struct mpc_cpu;
struct mpc_table;

/**
 * struct x86_init_mpparse - platform specific mpparse ops
 * @mpc_record:			platform specific mpc record accounting
 * @setup_ioapic_ids:		platform specific ioapic id override
 * @mpc_apic_id:		platform specific mpc apic id assignment
 * @smp_read_mpc_oem:		platform specific oem mpc table setup
 * @mpc_oem_pci_bus:		platform specific pci bus setup (default NULL)
 * @mpc_oem_bus_info:		platform specific mpc bus info
 * @find_smp_config:		find the smp configuration
 * @get_smp_config:		get the smp configuration
 */
struct x86_init_mpparse {
	void (*mpc_record)(unsigned int mode);
	void (*setup_ioapic_ids)(void);
	int (*mpc_apic_id)(struct mpc_cpu *m);
	void (*smp_read_mpc_oem)(struct mpc_table *mpc);
	void (*mpc_oem_pci_bus)(struct mpc_bus *m);
	void (*mpc_oem_bus_info)(struct mpc_bus *m, char *name);
	void (*find_smp_config)(void);
	void (*get_smp_config)(unsigned int early);
};

/**
 * struct x86_init_resources - platform specific resource related ops
 * @probe_roms:			probe BIOS roms
 * @reserve_resources:		reserve the standard resources for the
 *				platform
 * @memory_setup:		platform specific memory setup
 *
 */
struct x86_init_resources {
	void (*probe_roms)(void);
	void (*reserve_resources)(void);
	char *(*memory_setup)(void);
};

/**
 * struct x86_init_irqs - platform specific interrupt setup
 * @pre_vector_init:		init code to run before interrupt vectors
 *				are set up.
 * @intr_init:			interrupt init code
 * @trap_init:			platform specific trap setup
 */
struct x86_init_irqs {
	void (*pre_vector_init)(void);
	void (*intr_init)(void);
	void (*trap_init)(void);
};

/**
 * struct x86_init_oem - oem platform specific customizing functions
 * @arch_setup:			platform specific architecure setup
 * @banner:			print a platform specific banner
 */
struct x86_init_oem {
	void (*arch_setup)(void);
	void (*banner)(void);
};

/**
 * struct x86_init_paging - platform specific paging functions
 * @pagetable_setup_start:	platform specific pre paging_init() call
 * @pagetable_setup_done:	platform specific post paging_init() call
 */
struct x86_init_paging {
	void (*pagetable_setup_start)(pgd_t *base);
	void (*pagetable_setup_done)(pgd_t *base);
};

/**
 * struct x86_init_timers - platform specific timer setup
 * @setup_perpcu_clockev:	set up the per cpu clock event device for the
 *				boot cpu
 * @tsc_pre_init:		platform function called before TSC init
 * @timer_init:			initialize the platform timer (default PIT/HPET)
 */
struct x86_init_timers {
	void (*setup_percpu_clockev)(void);
	void (*tsc_pre_init)(void);
	void (*timer_init)(void);
};

/**
 * struct x86_init_iommu - platform specific iommu setup
 * @iommu_init:			platform specific iommu setup
 */
struct x86_init_iommu {
	int (*iommu_init)(void);
};

/**
 * struct x86_init_ops - functions for platform specific setup
 *
 */
struct x86_init_ops {
	struct x86_init_resources	resources;
	struct x86_init_mpparse		mpparse;
	struct x86_init_irqs		irqs;
	struct x86_init_oem		oem;
	struct x86_init_paging		paging;
	struct x86_init_timers		timers;
	struct x86_init_iommu		iommu;
};

/**
 * struct x86_cpuinit_ops - platform specific cpu hotplug setups
 * @setup_percpu_clockev:	set up the per cpu clock event device
 */
struct x86_cpuinit_ops {
	void (*setup_percpu_clockev)(void);
};

/**
 * struct x86_platform_ops - platform specific runtime functions
 * @calibrate_tsc:		calibrate TSC
 * @get_wallclock:		get time from HW clock like RTC etc.
 * @set_wallclock:		set time back to HW clock
 * @is_untracked_pat_range	exclude from PAT logic
 */
struct x86_platform_ops {
	unsigned long (*calibrate_tsc)(void);
	unsigned long (*get_wallclock)(void);
	int (*set_wallclock)(unsigned long nowtime);
	void (*iommu_shutdown)(void);
	bool (*is_untracked_pat_range)(u64 start, u64 end);
};

extern struct x86_init_ops x86_init;
extern struct x86_cpuinit_ops x86_cpuinit;
extern struct x86_platform_ops x86_platform;

extern void x86_init_noop(void);
extern void x86_init_uint_noop(unsigned int unused);


#line 9 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mpspec.h" 2

extern int apic_version[256];
extern int pic_mode;


/*
 * Summit or generic (i.e. installer) kernels need lots of bus entries.
 * Maximum 256 PCI busses, plus 1 ISA bus in each of 4 cabinets.
 */


 



extern unsigned int def_to_bigsmp;
extern u8 apicid_2_node[];

#if definedEx(CONFIG_X86_NUMAQ)
extern int mp_bus_id_to_node[260];
extern int mp_bus_id_to_local[260];
extern int quad_local_to_mp_bus_id [1/4][4];
#endif


 




#if (definedEx(CONFIG_MCA) || definedEx(CONFIG_EISA))
extern int mp_bus_id_to_type[260];
#endif
extern unsigned long mp_bus_not_pci[(((260) + (8 * sizeof(long)) - 1) / (8 * sizeof(long)))];

extern unsigned int boot_cpu_physical_apicid;
extern unsigned int max_physical_apicid;
extern int mpc_default_type;
extern unsigned long mp_lapic_addr;

#if definedEx(CONFIG_X86_LOCAL_APIC)
extern int smp_found_config;
#endif


static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void get_smp_config(void)
{
	x86_init.mpparse.get_smp_config(0);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void early_get_smp_config(void)
{
	x86_init.mpparse.get_smp_config(1);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void find_smp_config(void)
{
	x86_init.mpparse.find_smp_config();
}

#if definedEx(CONFIG_X86_MPPARSE)
extern void early_reserve_e820_mpc_new(void);
extern int enable_update_mptable;
extern int default_mpc_apic_id(struct mpc_cpu *m);
extern void default_smp_read_mpc_oem(struct mpc_table *mpc);
#if definedEx(CONFIG_X86_IO_APIC)
extern void default_mpc_oem_bus_info(struct mpc_bus *m, char *str);
#endif
#if !(definedEx(CONFIG_X86_IO_APIC))

#endif
extern void default_find_smp_config(void);
extern void default_get_smp_config(unsigned int early);
#endif
#if !(definedEx(CONFIG_X86_MPPARSE))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void early_reserve_e820_mpc_new(void) { }






#endif
void __attribute__ ((__section__(".cpuinit.text")))  generic_processor_info(int apicid, int version);
#if definedEx(CONFIG_ACPI)
extern void mp_register_ioapic(int id, u32 address, u32 gsi_base);
extern void mp_override_legacy_irq(u8 bus_irq, u8 polarity, u8 trigger,
				   u32 gsi);
extern void mp_config_acpi_legacy_irqs(void);
struct device;
extern int mp_register_gsi(struct device *dev, u32 gsi, int edge_level,
				 int active_high_low);
extern int acpi_probe_gsi(void);
#if definedEx(CONFIG_X86_IO_APIC)
extern int mp_find_ioapic(int gsi);
extern int mp_find_ioapic_pin(int ioapic, int gsi);
#endif
#endif
#if !(definedEx(CONFIG_ACPI))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int acpi_probe_gsi(void)
{
	return 0;
}
#endif


struct physid_mask {
	unsigned long mask[(((256) + (8 * sizeof(long)) - 1) / (8 * sizeof(long)))];
};

typedef struct physid_mask physid_mask_t;


































static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long physids_coerce(physid_mask_t *map)
{
	return map->mask[0];
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void physids_promote(unsigned long physids, physid_mask_t *map)
{
	bitmap_zero((*map).mask, 256);
	map->mask[0] = physids;
}

/* Note: will create very large stack frames if physid_mask_t is big */







static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void physid_set_mask_of_physid(int physid, physid_mask_t *map)
{
	bitmap_zero((*map).mask, 256);
	set_bit(physid, (*map).mask);
}




extern physid_mask_t phys_cpu_present_map;

extern int generic_mps_oem_check(struct mpc_table *, char *, char *);

extern int default_acpi_madt_oem_check(char *, char *);


#line 14 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/smp.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/cpumask.h" 1

























 





 























































 












	

	






	





	




	



					    
					    

	




					   

	






 







	











	
	
		
	











	
	
		
	





















































	









	





















	











	








	








	









			       
			       

	
				       









			      

	
				      









			       
			       

	
				       









				  
				  

	
					  








				      

	
					      








				

	
						 








				     

	
						      








				 

	
						  








	








	








	









				       

	
					       









				      

	
					      








				

	












































				    

	











				     

	












				    

	
				    












	









	

	





























 



	



					  

	




	
	



					  

	
	



















































	










	



	
	
	










 



























 






















 














 











	





	





	





	








	




					

	




					

	




					

	





					

	




					

	




					

	




					

	





	





	





					

	



#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/delay.h" 1



/*
 * Copyright (C) 1993 Linus Torvalds
 *
 * Delay routines, using a pre-computed "loops_per_jiffy" value.
 */

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 1





























































 
























































 


  












   











 

	





	




	

	









	

	

	

	

	

	

	



	

	







































	

	




				   
















 
	


	

















	





	




	
		

















	
	
	
	
	
	

















	
	
	


				
				

				
				

			







	
	
	


























 






 


 
















 





















 




























 






















































	



	



























 











	




	

































































































































	
	
	
	
	
	
	
	
	
	
	
	
	
	

























 






#line 12 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/delay.h" 2

extern unsigned long loops_per_jiffy;

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/delay.h" 1



/*
 * Copyright (C) 1993 Linus Torvalds
 *
 * Delay routines calling functions in arch/x86/lib/delay.c
 */

/* Undefined functions to get compile-time errors */
extern void __bad_udelay(void);
extern void __bad_ndelay(void);

extern void __udelay(unsigned long usecs);
extern void __ndelay(unsigned long nsecs);
extern void __const_udelay(unsigned long xloops);
extern void __delay(unsigned long loops);

/* 0x10c7 is 2**32 / 1000000 (rounded up) */




/* 0x5 is 2**32 / 1000000000 (rounded up) */




void use_tsc_delay(void);


#line 16 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/delay.h" 2

/*
 * Using udelay() for intervals greater than a few milliseconds can
 * risk overflow for high loops_per_jiffy (high bogomips) machines. The
 * mdelay() provides a wrapper to prevent this.  For delays greater
 * than MAX_UDELAY_MS milliseconds, the wrapper is used.  Architecture
 * specific values can be defined in asm-???/delay.h as an override.
 * The 2nd mdelay() definition ensures GCC will optimize away the 
 * while loop for the common cases where n <= MAX_UDELAY_MS  --  Paul G.
 */












	



extern unsigned long lpj_fine;
void calibrate_delay(void);
void msleep(unsigned int msecs);
unsigned long msleep_interruptible(unsigned int msecs);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void ssleep(unsigned int seconds)
{
	msleep(seconds * 1000);
}


#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/pm.h" 1
/*
 *  pm.h - Power management interface
 *
 *  Copyright (C) 2000 Andrew Henroid
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/list.h" 1



















	









	
	










			      
			      

	
	
	
	

 
			      
			      











	













	











	
	











	
	
	

 









				

	
	
	
	



					

	
	








	
	









	
	








				  

	
	








				

	








	

















	
	








	



		

	
	
	
	
	
	
	

















		

	
		
	
		
		
	
		
	
		



				 
				 

	
	

	
	

	
	








				

	
		








				

	
		










				    

	
		
		
	











					 

	
		
		
	























































































































































































































	



	







	
	




	




	




	
	
	
	
		




	
	
	




	
		
		
	




	
	
	
		
	
	




					

	
	
	
	



					

	
	
	

	
		







				   

	
	
		
	































































#line 26 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/pm.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/workqueue.h" 1
/*
 * workqueue.h --- work queue handling for Linux.
 */




#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/timer.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/list.h" 1



















	









	
	










			      
			      

	
	
	
	

 
			      
			      











	













	











	
	











	
	
	

 









				

	
	
	
	



					

	
	








	
	









	
	








				  

	
	








				

	








	

















	
	








	



		

	
	
	
	
	
	
	

















		

	
		
	
		
		
	
		
	
		



				 
				 

	
	

	
	

	
	








				

	
		








				

	
		










				    

	
		
		
	











					 

	
		
		
	























































































































































































































	



	







	
	




	




	




	
	
	
	
		




	
	
	




	
		
		
	




	
	
	
		
	
	




					

	
	
	
	



					

	
	
	

	
		







				   

	
	
		
	































































#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/timer.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/ktime.h" 1
/*
 *  include/linux/ktime.h
 *
 *  ktime_t - nanosecond-resolution time format.
 *
 *   Copyright(C) 2005, Thomas Gleixner <tglx@linutronix.de>
 *   Copyright(C) 2005, Red Hat, Inc., Ingo Molnar
 *
 *  data type definitions, declarations, prototypes and macros.
 *
 *  Started by: Thomas Gleixner and Ingo Molnar
 *
 *  Credits:
 *
 *  	Roman Zippel provided the ideas and primary code snippets of
 *  	the ktime_t union and further simplifications of the original
 *  	code.
 *
 *  For licencing details see kernel-base/COPYING
 */



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/time.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/time.h" 2


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/cache.h" 1





















 










 














 






#line 9 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/time.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/seqlock.h" 1
































	
	



























	
	
	




	
	
	




	

	
		
		
	
	





	


	
	
	
		
		
	

	









	

	











	








	


	
	
	
		
		
	
	







	

	









	
	




	
	






























#line 10 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/time.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/math64.h" 1















	
	







	
	







	


 


	
	



















	
	








	
	







	

	
		

		

		
		
	

	

	



#line 11 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/time.h" 2



struct timespec {
	__kernel_time_t	tv_sec;			/* seconds */
	long		tv_nsec;		/* nanoseconds */
};

struct timeval {
	__kernel_time_t		tv_sec;		/* seconds */
	__kernel_suseconds_t	tv_usec;	/* microseconds */
};

struct timezone {
	int	tz_minuteswest;	/* minutes west of Greenwich */
	int	tz_dsttime;	/* type of dst correction */
};


extern struct timezone sys_tz;

/* Parameters used to convert the timespec values: */










static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int timespec_equal(const struct timespec *a,
                                 const struct timespec *b)
{
	return (a->tv_sec == b->tv_sec) && (a->tv_nsec == b->tv_nsec);
}

/*
 * lhs < rhs:  return <0
 * lhs == rhs: return 0
 * lhs > rhs:  return >0
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int timespec_compare(const struct timespec *lhs, const struct timespec *rhs)
{
	if (lhs->tv_sec < rhs->tv_sec)
		return -1;
	if (lhs->tv_sec > rhs->tv_sec)
		return 1;
	return lhs->tv_nsec - rhs->tv_nsec;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int timeval_compare(const struct timeval *lhs, const struct timeval *rhs)
{
	if (lhs->tv_sec < rhs->tv_sec)
		return -1;
	if (lhs->tv_sec > rhs->tv_sec)
		return 1;
	return lhs->tv_usec - rhs->tv_usec;
}

extern unsigned long mktime(const unsigned int year, const unsigned int mon,
			    const unsigned int day, const unsigned int hour,
			    const unsigned int min, const unsigned int sec);

extern void set_normalized_timespec(struct timespec *ts, time_t sec, s64 nsec);
extern struct timespec timespec_add_safe(const struct timespec lhs,
					 const struct timespec rhs);

/*
 * sub = lhs - rhs, in normalized form
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 struct timespec timespec_sub(struct timespec lhs,
						struct timespec rhs)
{
	struct timespec ts_delta;
	set_normalized_timespec(&ts_delta, lhs.tv_sec - rhs.tv_sec,
				lhs.tv_nsec - rhs.tv_nsec);
	return ts_delta;
}

/*
 * Returns true if the timespec is norm, false if denorm:
 */



extern struct timespec xtime;
extern struct timespec wall_to_monotonic;
extern seqlock_t xtime_lock;

extern void read_persistent_clock(struct timespec *ts);
extern void read_boot_clock(struct timespec *ts);
extern int update_persistent_clock(struct timespec now);
extern int no_sync_cmos_clock __attribute__((__section__(".data.read_mostly")));
void timekeeping_init(void);
extern int timekeeping_suspended;

unsigned long get_seconds(void);
struct timespec current_kernel_time(void);
struct timespec __current_kernel_time(void); /* does not hold xtime_lock */
struct timespec get_monotonic_coarse(void);




/* Some architectures do not supply their own clocksource.
 * This is mainly the case in architectures that get their
 * inter-tick times by reading the counter on their interval
 * timer. Since these timers wrap every tick, they're not really
 * useful as clocksources. Wrapping them to act like one is possible
 * but not very efficient. So we provide a callout these arches
 * can implement for use with the jiffies clocksource to provide
 * finer then tick granular time.
 */
#if definedEx(CONFIG_ARCH_USES_GETTIMEOFFSET)
extern u32 arch_gettimeoffset(void);
#endif
#if !(definedEx(CONFIG_ARCH_USES_GETTIMEOFFSET))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 u32 arch_gettimeoffset(void) { return 0; }
#endif
extern void do_gettimeofday(struct timeval *tv);
extern int do_settimeofday(struct timespec *tv);
extern int do_sys_settimeofday(struct timespec *tv, struct timezone *tz);

extern long do_utimes(int dfd, char  *filename, struct timespec *times, int flags);
struct itimerval;
extern int do_setitimer(int which, struct itimerval *value,
			struct itimerval *ovalue);
extern unsigned int alarm_setitimer(unsigned int seconds);
extern int do_getitimer(int which, struct itimerval *value);
extern void getnstimeofday(struct timespec *tv);
extern void getrawmonotonic(struct timespec *ts);
extern void getboottime(struct timespec *ts);
extern void monotonic_to_bootbased(struct timespec *ts);

extern struct timespec timespec_trunc(struct timespec t, unsigned gran);
extern int timekeeping_valid_for_hres(void);
extern u64 timekeeping_max_deferment(void);
extern void update_wall_time(void);
extern void update_xtime_cache(u64 nsec);
extern void timekeeping_leap_insert(int leapsecond);

struct tms;
extern void do_sys_times(struct tms *);

/*
 * Similar to the struct tm in userspace <time.h>, but it needs to be here so
 * that the kernel source is self contained.
 */
struct tm {
	/*
	 * the number of seconds after the minute, normally in the range
	 * 0 to 59, but can be up to 60 to allow for leap seconds
	 */
	int tm_sec;
	/* the number of minutes after the hour, in the range 0 to 59*/
	int tm_min;
	/* the number of hours past midnight, in the range 0 to 23 */
	int tm_hour;
	/* the day of the month, in the range 1 to 31 */
	int tm_mday;
	/* the number of months since January, in the range 0 to 11 */
	int tm_mon;
	/* the number of years since 1900 */
	long tm_year;
	/* the number of days since Sunday, in the range 0 to 6 */
	int tm_wday;
	/* the number of days since January 1, in the range 0 to 365 */
	int tm_yday;
};

void time_to_tm(time_t totalsecs, int offset, struct tm *result);

/**
 * timespec_to_ns - Convert timespec to nanoseconds
 * @ts:		pointer to the timespec variable to be converted
 *
 * Returns the scalar nanosecond representation of the timespec
 * parameter.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 s64 timespec_to_ns(const struct timespec *ts)
{
	return ((s64) ts->tv_sec * 1000000000L) + ts->tv_nsec;
}

/**
 * timeval_to_ns - Convert timeval to nanoseconds
 * @ts:		pointer to the timeval variable to be converted
 *
 * Returns the scalar nanosecond representation of the timeval
 * parameter.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 s64 timeval_to_ns(const struct timeval *tv)
{
	return ((s64) tv->tv_sec * 1000000000L) +
		tv->tv_usec * 1000L;
}

/**
 * ns_to_timespec - Convert nanoseconds to timespec
 * @nsec:	the nanoseconds value to be converted
 *
 * Returns the timespec representation of the nsec parameter.
 */
extern struct timespec ns_to_timespec(const s64 nsec);

/**
 * ns_to_timeval - Convert nanoseconds to timeval
 * @nsec:	the nanoseconds value to be converted
 *
 * Returns the timeval representation of the nsec parameter.
 */
extern struct timeval ns_to_timeval(const s64 nsec);

/**
 * timespec_add_ns - Adds nanoseconds to a timespec
 * @a:		pointer to timespec to be incremented
 * @ns:		unsigned nanoseconds value to be added
 *
 * This must always be inlined because its used from the x86-64 vdso,
 * which cannot call other kernel functions.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline)) void timespec_add_ns(struct timespec *a, u64 ns)
{
	a->tv_sec += __iter_div_u64_rem(a->tv_nsec + ns, 1000000000L, &ns);
	a->tv_nsec = ns;
}









/*
 * Names of the interval timers, and structure
 * defining a timer setting:
 */




struct itimerspec {
	struct timespec it_interval;	/* timer period */
	struct timespec it_value;	/* timer expiration */
};

struct itimerval {
	struct timeval it_interval;	/* timer interval */
	struct timeval it_value;	/* current value */
};

/*
 * The IDs of the various system clocks (for POSIX.1b interval timers):
 */








/*
 * The IDs of various hardware clocks:
 */





/*
 * The various flags for setting POSIX.1b interval timers:
 */



#line 26 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/ktime.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/jiffies.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/math64.h" 1















	
	







	
	







	


 


	
	



















	
	








	
	







	

	
		

		

		
		
	

	

	



#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/jiffies.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 1





























































 
























































 


  












   











 

	





	




	

	









	

	

	

	

	

	

	



	

	







































	

	




				   
















 
	


	

















	





	




	
		

















	
	
	
	
	
	

















	
	
	


				
				

				
				

			







	
	
	


























 






 


 
















 





















 




























 






















































	



	



























 











	




	

































































































































	
	
	
	
	
	
	
	
	
	
	
	
	
	

























 






#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/jiffies.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/jiffies.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/time.h" 1













	
	



	
	



	
	

















                                 

	









	
		
	
		
	




	
		
	
		
	



			    
			    



					 





						

	
	
				
	






































 








			






















	



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	













	











	
		




























	
	



















	
	



	
	



























#line 9 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/jiffies.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/timex.h" 1
/*****************************************************************************
 *                                                                           *
 * Copyright (c) David L. Mills 1993                                         *
 *                                                                           *
 * Permission to use, copy, modify, and distribute this software and its     *
 * documentation for any purpose and without fee is hereby granted, provided *
 * that the above copyright notice appears in all copies and that both the   *
 * copyright notice and this permission notice appear in supporting          *
 * documentation, and that the name University of Delaware not be used in    *
 * advertising or publicity pertaining to distribution of the software       *
 * without specific, written prior permission.  The University of Delaware   *
 * makes no representations about the suitability this software for any      *
 * purpose.  It is provided "as is" without express or implied warranty.     *
 *                                                                           *
 *****************************************************************************/

/*
 * Modification history timex.h
 *
 * 29 Dec 97	Russell King
 *	Moved CLOCK_TICK_RATE, CLOCK_TICK_FACTOR and FINETUNE to asm/timex.h
 *	for ARM machines
 *
 *  9 Jan 97    Adrian Sun
 *      Shifted LATCH define to allow access to alpha machines.
 *
 * 26 Sep 94	David L. Mills
 *	Added defines for hybrid phase/frequency-lock loop.
 *
 * 19 Mar 94	David L. Mills
 *	Moved defines from kernel routines to header file and added new
 *	defines for PPS phase-lock loop.
 *
 * 20 Feb 94	David L. Mills
 *	Revised status codes and structures for external clock and PPS
 *	signal discipline.
 *
 * 28 Nov 93	David L. Mills
 *	Adjusted parameters to improve stability and increase poll
 *	interval.
 *
 * 17 Sep 93    David L. Mills
 *      Created file $NTP/include/sys/timex.h
 * 07 Oct 93    Torsten Duwe
 *      Derived linux/timex.h
 * 1995-08-13    Torsten Duwe
 *      kernel PLL updated to 1994-12-13 specs (rfc-1589)
 * 1997-08-30    Ulrich Windl
 *      Added new constant NTP_PHASE_LIMIT
 * 2004-08-12    Christoph Lameter
 *      Reworked time interpolation logic
 */



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/time.h" 1













	
	



	
	



	
	

















                                 

	









	
		
	
		
	




	
		
	
		
	



			    
			    



					 





						

	
	
				
	






































 








			






















	



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	













	











	
		




























	
	



















	
	



	
	



























#line 58 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/timex.h" 2



/*
 * syscall interface - used (mainly by NTP daemon)
 * to discipline kernel clock oscillator
 */
struct timex {
	unsigned int modes;	/* mode selector */
	long offset;		/* time offset (usec) */
	long freq;		/* frequency offset (scaled ppm) */
	long maxerror;		/* maximum error (usec) */
	long esterror;		/* estimated error (usec) */
	int status;		/* clock command/status */
	long constant;		/* pll time constant */
	long precision;		/* clock precision (usec) (read only) */
	long tolerance;		/* clock frequency tolerance (ppm)
				 * (read only)
				 */
	struct timeval time;	/* (read only) */
	long tick;		/* (modified) usecs between clock ticks */

	long ppsfreq;           /* pps frequency (scaled ppm) (ro) */
	long jitter;            /* pps jitter (us) (ro) */
	int shift;              /* interval duration (s) (shift) (ro) */
	long stabil;            /* pps stability (scaled ppm) (ro) */
	long jitcnt;            /* jitter limit exceeded (ro) */
	long calcnt;            /* calibration intervals (ro) */
	long errcnt;            /* calibration errors (ro) */
	long stbcnt;            /* stability limit exceeded (ro) */

	int tai;		/* TAI offset (ro) */

	int  :32; int  :32; int  :32; int  :32;
	int  :32; int  :32; int  :32; int  :32;
	int  :32; int  :32; int  :32;
};

/*
 * Mode codes (timex.mode)
 */















 


/* NTP userland likes the MOD_ prefix better */











/*
 * Status codes (timex.status)
 */




















/* read-only bits */



/*
 * Clock states (time_state)
 */









#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/compiler.h" 1



















 


































	
	
	
	
		
			
			
		
		
			
			
		
		
	





























































 






























 
















































































































#line 171 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/timex.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 172 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/timex.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/param.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/param.h" 1
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/param.h" 1




















#line 3 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/param.h" 2
#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/param.h" 2


#line 173 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/timex.h" 2

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/timex.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 1







































	

	

	





 









	
	
	
	

	

	
	
	
	
	
	
	
	
 	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	

	
	
	
	
	

	
	
	
	
	
	
	
	

	






























 







	
 	





















				

	
	
	    
	      
	      
	      
	    




	





	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


 
	
	
	
	
	
	
	
	
	
	













	


	

	





	

	


	









	





	
	
	
	
	
	
	

	
	

	
	



	
	
	
	
	
		
			
			
		
		
			
			
			
			
		
	
	
	

	
	

	
	

	

	
		
		
	




	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



	
	



	
	
	



	
	
	
	



	
	
	
	






	
	




	
		
		
	









 







	
	











	
	
	
	

	
 	
	
	
	
	


	


	

	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	

	
	
	
	
	

	
	
	




	

	
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
	
	




	
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
	








	

	
		      
		      
		      
		      
		      
		      
		      






	

	
	
		
		
	






	





 











			    

	














	

	
	
	
	




	

	
	
	
	



	






















			 
			 

	
	
	




			       
			       

	
	
	







	

	

	




	

	

	




	

	

	




	

	

	





	




	





	


	
		

		
	

		


		
			     



			     

	
	
		     




	
	
		     




	
	
	
		     

























	
	
	
		
	
		
			















    


	
		

	

    




	
	


	
		

	
	

	





	
		

	



					     


	
		

	
		     
		     























 









	
			  
			  
			  









	
			  
			  
			  




	






























































 




































					       



















	




	

	
	






				    

	
	
	

	
	
		

	



#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/timex.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/tsc.h" 1
/*
 * x86 TSC related functions
 */



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 1







































	

	

	





 









	
	
	
	

	

	
	
	
	
	
	
	
	
 	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	

	
	
	
	
	

	
	
	
	
	
	
	
	

	






























 







	
 	





















				

	
	
	    
	      
	      
	      
	    




	





	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


 
	
	
	
	
	
	
	
	
	
	













	


	

	





	

	


	









	





	
	
	
	
	
	
	

	
	

	
	



	
	
	
	
	
		
			
			
		
		
			
			
			
			
		
	
	
	

	
	

	
	

	

	
		
		
	




	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



	
	



	
	
	



	
	
	
	



	
	
	
	






	
	




	
		
		
	









 







	
	











	
	
	
	

	
 	
	
	
	
	


	


	

	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	

	
	
	
	
	

	
	
	




	

	
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
	
	




	
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
	








	

	
		      
		      
		      
		      
		      
		      
		      






	

	
	
		
		
	






	





 











			    

	














	

	
	
	
	




	

	
	
	
	



	






















			 
			 

	
	
	




			       
			       

	
	
	







	

	

	




	

	

	




	

	

	




	

	

	





	




	





	


	
		

		
	

		


		
			     



			     

	
	
		     




	
	
		     




	
	
	
		     

























	
	
	
		
	
		
			















    


	
		

	

    




	
	


	
		

	
	

	





	
		

	



					     


	
		

	
		     
		     























 









	
			  
			  
			  









	
			  
			  
			  




	






























































 




































					       



















	




	

	
	






				    

	
	
	

	
	
		

	



#line 9 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/tsc.h" 2




/*
 * Standard way to access the cycle counter.
 */
typedef unsigned long long cycles_t;

extern unsigned int cpu_khz;
extern unsigned int tsc_khz;

extern void disable_TSC(void);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 cycles_t get_cycles(void)
{
	unsigned long long ret = 0;


	
		

	((ret) = __native_read_tsc());

	return ret;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline)) cycles_t vget_cycles(void)
{
	/*
	 * We only do VDSOs on TSC capable CPUs, so this shouldnt
	 * access boot_cpu_data (which is not VDSO-safe):
	 */

	
		

	return (cycles_t)__native_read_tsc();
}

extern void tsc_init(void);
extern void mark_tsc_unstable(char *reason);
extern int unsynchronized_tsc(void);
extern int check_tsc_unstable(void);
extern unsigned long native_calibrate_tsc(void);

/*
 * Boot-time check whether the TSCs are synchronized across
 * all CPUs/cores:
 */
extern void check_tsc_sync_source(int cpu);
extern void check_tsc_sync_target(void);

extern int notsc_setup(char *);


#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/timex.h" 2

/* Assume we use the PIT time source for the clock tick */





#line 175 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/timex.h" 2

/*
 * SHIFT_PLL is used as a dampening factor to define how much we
 * adjust the frequency correction for a given offset in PLL mode.
 * It also used in dampening the offset correction, to define how
 * much of the current value in time_offset we correct for each
 * second. Changing this value changes the stiffness of the ntp
 * adjustment code. A lower value makes it more flexible, reducing
 * NTP convergence time. A higher value makes it stiffer, increasing
 * convergence time, but making the clock more stable.
 *
 * In David Mills' nanokernel reference implementation SHIFT_PLL is 4.
 * However this seems to increase convergence time much too long.
 *
 * https://lists.ntp.org/pipermail/hackers/2008-January/003487.html
 *
 * In the above mailing list discussion, it seems the value of 4
 * was appropriate for other Unix systems with HZ=100, and that
 * SHIFT_PLL should be decreased as HZ increases. However, Linux's
 * clock steering implementation is HZ independent.
 *
 * Through experimentation, a SHIFT_PLL value of 2 was found to allow
 * for fast convergence (very similar to the NTPv3 code used prior to
 * v2.6.19), with good clock stability.
 *
 *
 * SHIFT_FLL is used as a dampening factor to define how much we
 * adjust the frequency correction for a given offset in FLL mode.
 * In David Mills' nanokernel reference implementation SHIFT_FLL is 2.
 *
 * MAXTC establishes the maximum time constant of the PLL.
 */




/*
 * SHIFT_USEC defines the scaling (shift) of the time_freq and
 * time_tolerance variables, which represent the current frequency
 * offset and maximum frequency tolerance.
 */













/*
 * kernel variables
 * Note: maximum error = NTP synch distance = dispersion + delay / 2;
 * estimated error = NTP dispersion.
 */
extern unsigned long tick_usec;		/* USER_HZ period (usec) */
extern unsigned long tick_nsec;		/* ACTHZ          period (nsec) */
extern int tickadj;			/* amount of adjustment per tick */

/*
 * phase-lock loop variables
 */
extern int time_status;		/* clock synchronization status bits */
extern long time_maxerror;	/* maximum error */
extern long time_esterror;	/* estimated error */

extern long time_adjust;	/* The amount of adjtime left */

extern void ntp_init(void);
extern void ntp_clear(void);

/**
 * ntp_synced - Returns 1 if the NTP status is not UNSYNC
 *
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int ntp_synced(void)
{
	return !(time_status & 0x0040);
}

/* Required to safely shift negative values */











/* Returns how long ticks are at present, in ns / 2^NTP_SCALE_SHIFT. */
extern u64 tick_length;

extern void second_overflow(void);
extern void update_ntp_one_tick(void);
extern int do_adjtimex(struct timex *);

/* Don't use! Compatibility define for existing users. */


int read_current_timer(unsigned long *timer_val);

/* The clock frequency of the i8253/i8254 PIT */




#line 10 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/jiffies.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/param.h" 1
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/param.h" 1




















#line 3 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/param.h" 2
#line 11 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/jiffies.h" 2

/*
 * The following defines establish the engineering parameters of the PLL
 * model. The HZ variable establishes the timer interrupt frequency, 100 Hz
 * for the SunOS kernel, 256 Hz for the Ultrix kernel and 1024 Hz for the
 * OSF/1 kernel. The SHIFT_HZ define expresses the same value as the
 * nearest power of two in order to avoid hardware multiply operations.
 */


 
 
 
#if 1

#endif

 
 
 
 
 

/* LATCH is used in the interval timer and ftape setup. */


/* Suppose we want to devide two numbers NOM and DEN: NOM/DEN, then we can
 * improve accuracy by shifting LSH bits, hence calculating:
 *     (NOM << LSH) / DEN
 * This however means trouble for large NOM, because (NOM << LSH) may no
 * longer fit in 32 bits. The following way of calculating this gives us
 * some slack, under the following conditions:
 *   - (NOM / DEN) fits in (32 - LSH) bits.
 *   - (NOM % DEN) fits in (32 - LSH) bits.
 */



/* HZ is the requested value. ACTHZ is actual HZ ("<< 8" is for accuracy) */


/* TICK_NSEC is the time between ticks in nsec assuming real ACTHZ */


/* TICK_USEC is the time between ticks in usec assuming fake USER_HZ */


/* TICK_USEC_TO_NSEC is the time between ticks in nsec assuming real ACTHZ and	*/
/* a value TUSEC for TICK_USEC (can be set bij adjtimex)		*/


/* some arch's have a small-data section that can be accessed register-relative
 * but that can only take up to, say, 4-byte variables. jiffies being part of
 * an 8-byte variable may not be correctly accessed unless we force the issue
 */


/*
 * The 64-bit value is not atomic - you MUST NOT read it
 * without sampling the sequence number in xtime_lock.
 * get_jiffies_64() will do this for you as appropriate.
 */
extern u64 __attribute__((section(".data"))) jiffies_64;
extern unsigned long volatile __attribute__((section(".data"))) jiffies;

#if !((definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT)))
u64 get_jiffies_64(void);
#endif
#if (definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 u64 get_jiffies_64(void)
{
	return (u64)jiffies;
}
#endif
/*
 *	These inlines deal with timer wrapping correctly. You are 
 *	strongly encouraged to use them
 *	1. Because people otherwise forget
 *	2. Because if the timer wrap changes in future you won't have to
 *	   alter your driver code.
 *
 * time_after(a,b) returns true if the time a is after time b.
 *
 * Do this with "<0" and ">=0" to only test the sign of the result. A
 * good compiler would generate better code (and a really good compiler
 * wouldn't care). Gcc is currently neither.
 */












/*
 * Calculate whether a is in the range of [b, c].
 */




/*
 * Calculate whether a is in the range of [b, c).
 */




/* Same as above, but does so with platform independent 64bit types.
 * These must be used when utilizing jiffies_64 (i.e. return value of
 * get_jiffies_64() */












/*
 * These four macros compare jiffies and 'a' for convenience.
 */

/* time_is_before_jiffies(a) return true if a is before jiffies */


/* time_is_after_jiffies(a) return true if a is after jiffies */


/* time_is_before_eq_jiffies(a) return true if a is before or equal to jiffies*/


/* time_is_after_eq_jiffies(a) return true if a is after or equal to jiffies*/


/*
 * Have the 32 bit jiffies value wrap 5 minutes after boot
 * so jiffies wrap bugs show up earlier.
 */


/*
 * Change timeval to jiffies, trying to avoid the
 * most obvious overflows..
 *
 * And some not so obvious.
 *
 * Note that we don't want to return LONG_MAX, because
 * for various timeout reasons we often end up having
 * to wait "jiffies+1" in order to guarantee that we wait
 * at _least_ "jiffies" - so "jiffies+1" had better still
 * be positive.
 */


extern unsigned long preset_lpj;

/*
 * We want to do realistic conversions of time so we need to use the same
 * values the update wall clock code uses as the jiffies size.  This value
 * is: TICK_NSEC (which is defined in timex.h).  This
 * is a constant and is in nanoseconds.  We will use scaled math
 * with a set of scales defined here as SEC_JIFFIE_SC,  USEC_JIFFIE_SC and
 * NSEC_JIFFIE_SC.  Note that these defines contain nothing but
 * constants and so are computed at compile time.  SHIFT_HZ (computed in
 * timex.h) adjusts the scaling for different HZ values.

 * Scaled math???  What is that?
 *
 * Scaled math is a way to do integer math on values that would,
 * otherwise, either overflow, underflow, or cause undesired div
 * instructions to appear in the execution path.  In short, we "scale"
 * up the operands so they take more bits (more precision, less
 * underflow), do the desired operation and then "scale" the result back
 * by the same amount.  If we do the scaling by shifting we avoid the
 * costly mpy and the dastardly div instructions.

 * Suppose, for example, we want to convert from seconds to jiffies
 * where jiffies is defined in nanoseconds as NSEC_PER_JIFFIE.  The
 * simple math is: jiff = (sec * NSEC_PER_SEC) / NSEC_PER_JIFFIE; We
 * observe that (NSEC_PER_SEC / NSEC_PER_JIFFIE) is a constant which we
 * might calculate at compile time, however, the result will only have
 * about 3-4 bits of precision (less for smaller values of HZ).
 *
 * So, we scale as follows:
 * jiff = (sec) * (NSEC_PER_SEC / NSEC_PER_JIFFIE);
 * jiff = ((sec) * ((NSEC_PER_SEC * SCALE)/ NSEC_PER_JIFFIE)) / SCALE;
 * Then we make SCALE a power of two so:
 * jiff = ((sec) * ((NSEC_PER_SEC << SCALE)/ NSEC_PER_JIFFIE)) >> SCALE;
 * Now we define:
 * #define SEC_CONV = ((NSEC_PER_SEC << SCALE)/ NSEC_PER_JIFFIE))
 * jiff = (sec * SEC_CONV) >> SCALE;
 *
 * Often the math we use will expand beyond 32-bits so we tell C how to
 * do this and pass the 64-bit result of the mpy through the ">> SCALE"
 * which should take the result back to 32-bits.  We want this expansion
 * to capture as much precision as possible.  At the same time we don't
 * want to overflow so we pick the SCALE to avoid this.  In this file,
 * that means using a different scale for each range of HZ values (as
 * defined in timex.h).
 *
 * For those who want to know, gcc will give a 64-bit result from a "*"
 * operator if the result is a long long AND at least one of the
 * operands is cast to long long (usually just prior to the "*" so as
 * not to confuse it into thinking it really has a 64-bit operand,
 * which, buy the way, it can do, but it takes more code and at least 2
 * mpys).

 * We also need to be aware that one second in nanoseconds is only a
 * couple of bits away from overflowing a 32-bit word, so we MUST use
 * 64-bits to get the full range time in nanoseconds.

 */

/*
 * Here are the scales we will use.  One for seconds, nanoseconds and
 * microseconds.
 *
 * Within the limits of cpp we do a rough cut at the SEC_JIFFIE_SC and
 * check if the sign bit is set.  If not, we bump the shift count by 1.
 * (Gets an extra bit of precision where we can use it.)
 * We know it is set for HZ = 1024 and HZ = 100 not for 1000.
 * Haven't tested others.

 * Limits of cpp (for #if expressions) only long (no long long), but
 * then we only need the most signicant bit.
 */
















/*
 * USEC_ROUND is used in the timeval to jiffie conversion.  See there
 * for more details.  It is the scaled resolution rounding value.  Note
 * that it is a 64-bit value.  Since, when it is applied, we are already
 * in jiffies (albit scaled), it is nothing but the bits we will shift
 * off.
 */

/*
 * The maximum jiffie value is (MAX_INT >> 1).  Here we translate that
 * into seconds.  The 64-bit case will overflow if we are not careful,
 * so use the messy SH_DIV macro to do it.  Still all constants.
 */
#if !((definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT)))


#endif
#if (definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT))



#endif
/*
 * Convert various time units to each other:
 */
extern unsigned int jiffies_to_msecs(const unsigned long j);
extern unsigned int jiffies_to_usecs(const unsigned long j);
extern unsigned long msecs_to_jiffies(const unsigned int m);
extern unsigned long usecs_to_jiffies(const unsigned int u);
extern unsigned long timespec_to_jiffies(const struct timespec *value);
extern void jiffies_to_timespec(const unsigned long jiffies,
				struct timespec *value);
extern unsigned long timeval_to_jiffies(const struct timeval *value);
extern void jiffies_to_timeval(const unsigned long jiffies,
			       struct timeval *value);
extern clock_t jiffies_to_clock_t(long x);
extern unsigned long clock_t_to_jiffies(unsigned long x);
extern u64 jiffies_64_to_clock_t(u64 x);
extern u64 nsec_to_clock_t(u64 x);
extern unsigned long nsecs_to_jiffies(u64 n);




#line 27 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/ktime.h" 2

/*
 * ktime_t:
 *
 * On 64-bit CPUs a single 64-bit variable is used to store the hrtimers
 * internal representation of time values in scalar nanoseconds. The
 * design plays out best on 64-bit CPUs, where most conversions are
 * NOPs and most arithmetic ktime_t operations are plain arithmetic
 * operations.
 *
 * On 32-bit CPUs an optimized representation of the timespec structure
 * is used to avoid expensive conversions from and to timespecs. The
 * endian-aware order of the tv struct members is choosen to allow
 * mathematical operations on the tv64 member of the union too, which
 * for certain operations produces better code.
 *
 * For architectures with efficient support for 64/32-bit conversions the
 * plain scalar nanosecond based representation can be selected by the
 * config switch CONFIG_KTIME_SCALAR.
 */
union ktime {
	s64	tv64;

	

	
 	

	

};

typedef union ktime ktime_t;		/* Kill this */


#if (definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT))

#endif
#if !((definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT)))

#endif
/*
 * ktime_t definitions when using the 64-bit scalar representation:
 */


/**
 * ktime_set - Set a ktime_t variable from a seconds/nanoseconds value
 * @secs:	seconds to set
 * @nsecs:	nanoseconds to set
 *
 * Return the ktime_t representation of the value
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 ktime_t ktime_set(const long secs, const unsigned long nsecs)
{
#if (definedEx(CONFIG_64BIT) && definedEx(CONFIG_64BIT))
	if (__builtin_expect(!!(secs >= (((s64)~((u64)1 << 63)) / 1000000000L)), 0))
		return (ktime_t){ .tv64 = ((s64)~((u64)1 << 63)) };
#endif
	return (ktime_t) { .tv64 = (s64)secs * 1000000000L + (s64)nsecs };
}

/* Subtract two ktime_t variables. rem = lhs -rhs: */



/* Add two ktime_t variables. res = lhs + rhs: */



/*
 * Add a ktime_t variable and a scalar nanosecond value.
 * res = kt + nsval:
 */



/*
 * Subtract a scalar nanosecod from a ktime_t variable
 * res = kt - nsval:
 */



/* convert a timespec to ktime_t format: */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 ktime_t timespec_to_ktime(struct timespec ts)
{
	return ktime_set(ts.tv_sec, ts.tv_nsec);
}

/* convert a timeval to ktime_t format: */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 ktime_t timeval_to_ktime(struct timeval tv)
{
	return ktime_set(tv.tv_sec, tv.tv_usec * 1000L);
}

/* Map the ktime_t to timespec conversion to ns_to_timespec function */


/* Map the ktime_t to timeval conversion to ns_to_timeval function */


/* Convert ktime_t to nanoseconds - NOP in the scalar storage format: */


 

















	











	

	
	
		

	











	

	
	







	
		

	




























	
			   	   










	
				   










	
				   










	
		
		










	



/**
 * ktime_equal - Compares two ktime_t variables to see if they are equal
 * @cmp1:	comparable1
 * @cmp2:	comparable2
 *
 * Compare two ktime_t variables, returns 1 if equal
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int ktime_equal(const ktime_t cmp1, const ktime_t cmp2)
{
	return cmp1.tv64 == cmp2.tv64;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 s64 ktime_to_us(const ktime_t kt)
{
	struct timeval tv = ns_to_timeval((kt).tv64);
	return (s64) tv.tv_sec * 1000000L + tv.tv_usec;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 s64 ktime_us_delta(const ktime_t later, const ktime_t earlier)
{
       return ktime_to_us(({ (ktime_t){ .tv64 = (later).tv64 - (earlier).tv64 }; }));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 ktime_t ktime_add_us(const ktime_t kt, const u64 usec)
{
	return ({ (ktime_t){ .tv64 = (kt).tv64 + (usec * 1000) }; });
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 ktime_t ktime_sub_us(const ktime_t kt, const u64 usec)
{
	return ({ (ktime_t){ .tv64 = (kt).tv64 - (usec * 1000) }; });
}

extern ktime_t ktime_add_safe(const ktime_t lhs, const ktime_t rhs);

/*
 * The resolution of the clocks. The resolution value is returned in
 * the clock_getres() system call to give application programmers an
 * idea of the (in)accuracy of timers. Timer values are rounded up to
 * this resolution values.
 */



/* Get the monotonic time in timespec format: */
extern void ktime_get_ts(struct timespec *ts);

/* Get the real (wall-) time in timespec format: */


static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 ktime_t ns_to_ktime(u64 ns)
{
	static const ktime_t ktime_zero = { .tv64 = 0 };
	return ({ (ktime_t){ .tv64 = (ktime_zero).tv64 + (ns) }; });
}


#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/timer.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/stddef.h" 1








 



	
	





 



#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/timer.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/debugobjects.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/list.h" 1



















	









	
	










			      
			      

	
	
	
	

 
			      
			      











	













	











	
	











	
	
	

 









				

	
	
	
	



					

	
	








	
	









	
	








				  

	
	








				

	








	

















	
	








	



		

	
	
	
	
	
	
	

















		

	
		
	
		
		
	
		
	
		



				 
				 

	
	

	
	

	
	








				

	
		








				

	
		










				    

	
		
		
	











					 

	
		
		
	























































































































































































































	



	







	
	




	




	




	
	
	
	
		




	
	
	




	
		
		
	




	
	
	
		
	
	




					

	
	
	
	



					

	
	
	

	
		







				   

	
	
		
	































































#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/debugobjects.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/spinlock.h" 1






















































































 


  
				   







 






 

 













 

 
 
 

	





	




	




	





















 















 





 




















































 







	










	




	




	














	














	




	




	




	




	




	









	




	




	




	




	




















#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/debugobjects.h" 2

enum debug_obj_state {
	ODEBUG_STATE_NONE,
	ODEBUG_STATE_INIT,
	ODEBUG_STATE_INACTIVE,
	ODEBUG_STATE_ACTIVE,
	ODEBUG_STATE_DESTROYED,
	ODEBUG_STATE_NOTAVAILABLE,
	ODEBUG_STATE_MAX,
};

struct debug_obj_descr;

/**
 * struct debug_obj - representaion of an tracked object
 * @node:	hlist node to link the object into the tracker list
 * @state:	tracked object state
 * @object:	pointer to the real object
 * @descr:	pointer to an object type specific debug description structure
 */
struct debug_obj {
	struct hlist_node	node;
	enum debug_obj_state	state;
	void			*object;
	struct debug_obj_descr	*descr;
};

/**
 * struct debug_obj_descr - object type specific debug description structure
 * @name:		name of the object typee
 * @fixup_init:		fixup function, which is called when the init check
 *			fails
 * @fixup_activate:	fixup function, which is called when the activate check
 *			fails
 * @fixup_destroy:	fixup function, which is called when the destroy check
 *			fails
 * @fixup_free:		fixup function, which is called when the free check
 *			fails
 */
struct debug_obj_descr {
	const char		*name;

	int (*fixup_init)	(void *addr, enum debug_obj_state state);
	int (*fixup_activate)	(void *addr, enum debug_obj_state state);
	int (*fixup_destroy)	(void *addr, enum debug_obj_state state);
	int (*fixup_free)	(void *addr, enum debug_obj_state state);
};

#if definedEx(CONFIG_DEBUG_OBJECTS)
extern void debug_object_init      (void *addr, struct debug_obj_descr *descr);
extern void
debug_object_init_on_stack(void *addr, struct debug_obj_descr *descr);
extern void debug_object_activate  (void *addr, struct debug_obj_descr *descr);
extern void debug_object_deactivate(void *addr, struct debug_obj_descr *descr);
extern void debug_object_destroy   (void *addr, struct debug_obj_descr *descr);
extern void debug_object_free      (void *addr, struct debug_obj_descr *descr);

extern void debug_objects_early_init(void);
extern void debug_objects_mem_init(void);
#endif
#if !(definedEx(CONFIG_DEBUG_OBJECTS))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void
debug_object_init      (void *addr, struct debug_obj_descr *descr) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void
debug_object_init_on_stack(void *addr, struct debug_obj_descr *descr) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void
debug_object_activate  (void *addr, struct debug_obj_descr *descr) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void
debug_object_deactivate(void *addr, struct debug_obj_descr *descr) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void
debug_object_destroy   (void *addr, struct debug_obj_descr *descr) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void
debug_object_free      (void *addr, struct debug_obj_descr *descr) { }

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void debug_objects_early_init(void) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void debug_objects_mem_init(void) { }
#endif
#if definedEx(CONFIG_DEBUG_OBJECTS_FREE)
extern void debug_check_no_obj_freed(const void *address, unsigned long size);
#endif
#if !(definedEx(CONFIG_DEBUG_OBJECTS_FREE))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void
debug_check_no_obj_freed(const void *address, unsigned long size) { }
#endif

#line 9 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/timer.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/stringify.h" 1












#line 10 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/timer.h" 2

struct tvec_base;

struct timer_list {
	struct list_head entry;
	unsigned long expires;

	void (*function)(unsigned long);
	unsigned long data;

	struct tvec_base *base;
#if definedEx(CONFIG_TIMER_STATS)
	void *start_site;
	char start_comm[16];
	int start_pid;
#endif
#if definedEx(CONFIG_LOCKDEP)
	struct lockdep_map lockdep_map;
#endif
};

extern struct tvec_base boot_tvec_bases;

#if definedEx(CONFIG_LOCKDEP)
/*
 * NB: because we have to copy the lockdep_map, setting the lockdep_map key
 * (second argument) here is required, otherwise it could be initialised to
 * the copy of the lockdep_map later! We use the pointer to and the string
 * "<file>:<line>" as the key resp. the name of the lockdep_map.
 */


#endif
#if !(definedEx(CONFIG_LOCKDEP))

#endif














void init_timer_key(struct timer_list *timer,
		    const char *name,
		    struct lock_class_key *key);
void init_timer_deferrable_key(struct timer_list *timer,
			       const char *name,
			       struct lock_class_key *key);

#if definedEx(CONFIG_LOCKDEP)






























#endif
#if !(definedEx(CONFIG_LOCKDEP))










#endif
#if definedEx(CONFIG_DEBUG_OBJECTS_TIMERS)
extern void init_timer_on_stack_key(struct timer_list *timer,
				    const char *name,
				    struct lock_class_key *key);
extern void destroy_timer_on_stack(struct timer_list *timer);
#endif
#if !(definedEx(CONFIG_DEBUG_OBJECTS_TIMERS))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void destroy_timer_on_stack(struct timer_list *timer) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void init_timer_on_stack_key(struct timer_list *timer,
					   const char *name,
					   struct lock_class_key *key)
{
	init_timer_key(timer, name, key);
}
#endif
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void setup_timer_key(struct timer_list * timer,
				const char *name,
				struct lock_class_key *key,
				void (*function)(unsigned long),
				unsigned long data)
{
	timer->function = function;
	timer->data = data;
	init_timer_key(timer, name, key);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void setup_timer_on_stack_key(struct timer_list *timer,
					const char *name,
					struct lock_class_key *key,
					void (*function)(unsigned long),
					unsigned long data)
{
	timer->function = function;
	timer->data = data;
	init_timer_on_stack_key(timer, name, key);
}

/**
 * timer_pending - is a timer pending?
 * @timer: the timer in question
 *
 * timer_pending will tell whether a given timer is currently pending,
 * or not. Callers must ensure serialization wrt. other operations done
 * to this timer, eg. interrupt contexts, or other CPUs on SMP.
 *
 * return value: 1 if the timer is pending, 0 if not.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int timer_pending(const struct timer_list * timer)
{
	return timer->entry.next != ((void *)0);
}

extern void add_timer_on(struct timer_list *timer, int cpu);
extern int del_timer(struct timer_list * timer);
extern int mod_timer(struct timer_list *timer, unsigned long expires);
extern int mod_timer_pending(struct timer_list *timer, unsigned long expires);
extern int mod_timer_pinned(struct timer_list *timer, unsigned long expires);



/*
 * The jiffies value which is added to now, when there is no timer
 * in the timer wheel:
 */


/*
 * Return when the next timer-wheel timeout occurs (in absolute jiffies),
 * locks the timer base and does the comparison against the given
 * jiffie.
 */
extern unsigned long get_next_timer_interrupt(unsigned long now);

/*
 * Timer-statistics info:
 */
#if definedEx(CONFIG_TIMER_STATS)
extern int timer_stats_active;



extern void init_timer_stats(void);

extern void timer_stats_update_stats(void *timer, pid_t pid, void *startf,
				     void *timerf, char *comm,
				     unsigned int timer_flag);

extern void __timer_stats_timer_set_start_info(struct timer_list *timer,
					       void *addr);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void timer_stats_timer_set_start_info(struct timer_list *timer)
{
	if (__builtin_expect(!!(!timer_stats_active), 1))
		return;
	__timer_stats_timer_set_start_info(timer, __builtin_return_address(0));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void timer_stats_timer_clear_start_info(struct timer_list *timer)
{
	timer->start_site = ((void *)0);
}
#endif
#if !(definedEx(CONFIG_TIMER_STATS))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void init_timer_stats(void)
{
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void timer_stats_timer_set_start_info(struct timer_list *timer)
{
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void timer_stats_timer_clear_start_info(struct timer_list *timer)
{
}
#endif
extern void add_timer(struct timer_list *timer);

#if definedEx(CONFIG_SMP)
  extern int try_to_del_timer_sync(struct timer_list *timer);
  extern int del_timer_sync(struct timer_list *timer);
#endif
#if !(definedEx(CONFIG_SMP))


#endif


extern void init_timers(void);
extern void run_local_timers(void);
struct hrtimer;
extern enum hrtimer_restart it_real_fn(struct hrtimer *);

unsigned long __round_jiffies(unsigned long j, int cpu);
unsigned long __round_jiffies_relative(unsigned long j, int cpu);
unsigned long round_jiffies(unsigned long j);
unsigned long round_jiffies_relative(unsigned long j);

unsigned long __round_jiffies_up(unsigned long j, int cpu);
unsigned long __round_jiffies_up_relative(unsigned long j, int cpu);
unsigned long round_jiffies_up(unsigned long j);
unsigned long round_jiffies_up_relative(unsigned long j);


#line 10 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/workqueue.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/linkage.h" 1








 














































































#line 11 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/workqueue.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/bitops.h" 1

























	
	
	
	




	
	
	
	
		
	




	









	









	









	









	









	









	




	
		
	













	
		
 

	












				    









					 










				   









				   









					
					




#line 12 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/workqueue.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/lockdep.h" 1
/*
 * Runtime locking correctness validator
 *
 *  Copyright (C) 2006,2007 Red Hat, Inc., Ingo Molnar <mingo@redhat.com>
 *  Copyright (C) 2007 Red Hat, Inc., Peter Zijlstra <pzijlstr@redhat.com>
 *
 * see Documentation/lockdep-design.txt for more details.
 */


























	



	








	


	

	


	

	
	
	

	


	
	

	




	

	



	

	


	

	
	


	
	





	
	
	
	



	
	
	
	
	

	
	



	
	
	
	
	
	
	










	
	
	

	
	








	
	
	
	

	



	






	
	
	
	
	











	













	
	
	
	

	
	

	
	












	
	

	
	
	
	






















			     





























				    

	


















			 
			 


			 






			   
			   


		

	












 

























































 















 





 







 

























 



 






 



 






 


 






 



 






 


 















 



#line 13 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/workqueue.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/atomic.h" 1

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/atomic_32.h" 1























	











	











	
		     
		     











	
		     
		     













	

	
		     
		     
	










	
		     










	
		     












	

	
		     
		     
	












	

	
		     
		     
	













	

	
		     
		     
	











	

	
	
		

	
	
	
		     
		     
	



	
	
	
	
	












	




	




	













	
	
	
		
			
		
		
			
		
	
	

























	

































	

	






	
		
		
		
			
			
		

	


































































































#line 4 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/atomic.h" 2
 

#line 14 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/workqueue.h" 2

struct workqueue_struct;

struct work_struct;
typedef void (*work_func_t)(struct work_struct *work);

/*
 * The first word is the work queue pointer and the flags rolled into
 * one
 */


struct work_struct {
	atomic_long_t data;




	struct list_head entry;
	work_func_t func;
#if definedEx(CONFIG_LOCKDEP)
	struct lockdep_map lockdep_map;
#endif
};




struct delayed_work {
	struct work_struct work;
	struct timer_list timer;
};

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 struct delayed_work *to_delayed_work(struct work_struct *work)
{
	return ({ const typeof( ((struct delayed_work *)0)->work ) *__mptr = (work); (struct delayed_work *)( (char *)__mptr - ((size_t) &((struct delayed_work *)0)->work) );});
}

struct execute_work {
	struct work_struct work;
};

#if definedEx(CONFIG_LOCKDEP)
/*
 * NB: because we have to copy the lockdep_map, setting _key
 * here is required, otherwise it could get initialised to the
 * copy of the lockdep_map!
 */


#endif
#if !(definedEx(CONFIG_LOCKDEP))

#endif


















/*
 * initialize a work item's function pointer
 */








#if definedEx(CONFIG_DEBUG_OBJECTS_WORK)
extern void __init_work(struct work_struct *work, int onstack);
extern void destroy_work_on_stack(struct work_struct *work);
#endif
#if !(definedEx(CONFIG_DEBUG_OBJECTS_WORK))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __init_work(struct work_struct *work, int onstack) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void destroy_work_on_stack(struct work_struct *work) { }
#endif
/*
 * initialize all of a work item in one go
 *
 * NOTE! No point in using "atomic_long_set()": using a direct
 * assignment of the work data initializer allows the compiler
 * to generate better code.
 */
#if definedEx(CONFIG_LOCKDEP)










#endif
#if !(definedEx(CONFIG_LOCKDEP))







#endif




























/**
 * work_pending - Find out whether a work item is currently pending
 * @work: The work item in question
 */



/**
 * delayed_work_pending - Find out whether a delayable work item is currently
 * pending
 * @work: The work item in question
 */



/**
 * work_clear_pending - for internal use only, mark a work item as not pending
 * @work: The work item in question
 */




extern struct workqueue_struct *
__create_workqueue_key(const char *name, int singlethread,
		       int freezeable, int rt, struct lock_class_key *key,
		       const char *lock_name);

#if definedEx(CONFIG_LOCKDEP)














#endif
#if !(definedEx(CONFIG_LOCKDEP))



#endif





extern void destroy_workqueue(struct workqueue_struct *wq);

extern int queue_work(struct workqueue_struct *wq, struct work_struct *work);
extern int queue_work_on(int cpu, struct workqueue_struct *wq,
			struct work_struct *work);
extern int queue_delayed_work(struct workqueue_struct *wq,
			struct delayed_work *work, unsigned long delay);
extern int queue_delayed_work_on(int cpu, struct workqueue_struct *wq,
			struct delayed_work *work, unsigned long delay);

extern void flush_workqueue(struct workqueue_struct *wq);
extern void flush_scheduled_work(void);
extern void flush_delayed_work(struct delayed_work *work);

extern int schedule_work(struct work_struct *work);
extern int schedule_work_on(int cpu, struct work_struct *work);
extern int schedule_delayed_work(struct delayed_work *work, unsigned long delay);
extern int schedule_delayed_work_on(int cpu, struct delayed_work *work,
					unsigned long delay);
extern int schedule_on_each_cpu(work_func_t func);
extern int current_is_keventd(void);
extern int keventd_up(void);

extern void init_workqueues(void);
int execute_in_process_context(work_func_t fn, struct execute_work *);

extern int flush_work(struct work_struct *work);

extern int cancel_work_sync(struct work_struct *work);

/*
 * Kill off a pending schedule_delayed_work().  Note that the work callback
 * function may still be running on return from cancel_delayed_work(), unless
 * it returns 1 and the work doesn't re-arm itself. Run flush_workqueue() or
 * cancel_work_sync() to wait on it.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int cancel_delayed_work(struct delayed_work *work)
{
	int ret;

	ret = 
#if (definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC) && !(definedEx(CONFIG_SMP)))
del_timer(&work->timer)
#endif
#if !((!(definedEx(CONFIG_SMP)) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES)))
del_timer_sync(&work->timer)
#endif
;
	if (ret)
		clear_bit(0, ((unsigned long *)(&(&work->work)->data)));
	return ret;
}

/*
 * Like above, but uses del_timer() instead of del_timer_sync(). This means,
 * if it returns 0 the timer function may be running and the queueing is in
 * progress.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int __cancel_delayed_work(struct delayed_work *work)
{
	int ret;

	ret = del_timer(&work->timer);
	if (ret)
		clear_bit(0, ((unsigned long *)(&(&work->work)->data)));
	return ret;
}

extern int cancel_delayed_work_sync(struct delayed_work *work);

/* Obsolete. use cancel_delayed_work_sync() */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif

void cancel_rearming_delayed_workqueue(struct workqueue_struct *wq,
					struct delayed_work *work)
{
	cancel_delayed_work_sync(work);
}

/* Obsolete. use cancel_delayed_work_sync() */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif

void cancel_rearming_delayed_work(struct delayed_work *work)
{
	cancel_delayed_work_sync(work);
}

#if !(definedEx(CONFIG_SMP))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 long work_on_cpu(unsigned int cpu, long (*fn)(void *), void *arg)
{
	return fn(arg);
}
#endif
#if definedEx(CONFIG_SMP)
long work_on_cpu(unsigned int cpu, long (*fn)(void *), void *arg);
#endif

#line 27 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/pm.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/spinlock.h" 1






















































































 


  
				   







 






 

 













 

 
 
 

	





	




	




	





















 















 





 




















































 







	










	




	




	














	














	




	




	




	




	




	









	




	




	




	




	




















#line 28 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/pm.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/wait.h" 1































	

	
	
	



	
	



	
	



	
	









































 



	
	
	



					

	
	
	




	








	






						

	



							

	





			








































































































































































































































































						   

	
	






					    

	









				      


					   








			













































				

	
		
	



















				

	
		
	

	


#line 29 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/pm.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/timer.h" 1












	
	

	
	

	

	
	
	


	














 
















		    
		    

			       
			       
































 












				    
				    

 

					   
					   

	



				
				
				
				

	
	
	



					
					
					
					

	
	
	














	


































				     
				     


					       



	
		
	




	

 














  
  
 




















#line 30 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/pm.h" 2

/*
 * Callbacks for platform drivers to implement.
 */
extern void (*pm_idle)(void);
extern void (*pm_power_off)(void);
extern void (*pm_power_off_prepare)(void);

/*
 * Device power management
 */

struct device;

typedef struct pm_message {
	int event;
} pm_message_t;

/**
 * struct dev_pm_ops - device PM callbacks
 *
 * Several driver power state transitions are externally visible, affecting
 * the state of pending I/O queues and (for drivers that touch hardware)
 * interrupts, wakeups, DMA, and other hardware state.  There may also be
 * internal transitions to various low power modes, which are transparent
 * to the rest of the driver stack (such as a driver that's ON gating off
 * clocks which are not in active use).
 *
 * The externally visible transitions are handled with the help of the following
 * callbacks included in this structure:
 *
 * @prepare: Prepare the device for the upcoming transition, but do NOT change
 *	its hardware state.  Prevent new children of the device from being
 *	registered after @prepare() returns (the driver's subsystem and
 *	generally the rest of the kernel is supposed to prevent new calls to the
 *	probe method from being made too once @prepare() has succeeded).  If
 *	@prepare() detects a situation it cannot handle (e.g. registration of a
 *	child already in progress), it may return -EAGAIN, so that the PM core
 *	can execute it once again (e.g. after the new child has been registered)
 *	to recover from the race condition.  This method is executed for all
 *	kinds of suspend transitions and is followed by one of the suspend
 *	callbacks: @suspend(), @freeze(), or @poweroff().
 *	The PM core executes @prepare() for all devices before starting to
 *	execute suspend callbacks for any of them, so drivers may assume all of
 *	the other devices to be present and functional while @prepare() is being
 *	executed.  In particular, it is safe to make GFP_KERNEL memory
 *	allocations from within @prepare().  However, drivers may NOT assume
 *	anything about the availability of the user space at that time and it
 *	is not correct to request firmware from within @prepare() (it's too
 *	late to do that).  [To work around this limitation, drivers may
 *	register suspend and hibernation notifiers that are executed before the
 *	freezing of tasks.]
 *
 * @complete: Undo the changes made by @prepare().  This method is executed for
 *	all kinds of resume transitions, following one of the resume callbacks:
 *	@resume(), @thaw(), @restore().  Also called if the state transition
 *	fails before the driver's suspend callback (@suspend(), @freeze(),
 *	@poweroff()) can be executed (e.g. if the suspend callback fails for one
 *	of the other devices that the PM core has unsuccessfully attempted to
 *	suspend earlier).
 *	The PM core executes @complete() after it has executed the appropriate
 *	resume callback for all devices.
 *
 * @suspend: Executed before putting the system into a sleep state in which the
 *	contents of main memory are preserved.  Quiesce the device, put it into
 *	a low power state appropriate for the upcoming system state (such as
 *	PCI_D3hot), and enable wakeup events as appropriate.
 *
 * @resume: Executed after waking the system up from a sleep state in which the
 *	contents of main memory were preserved.  Put the device into the
 *	appropriate state, according to the information saved in memory by the
 *	preceding @suspend().  The driver starts working again, responding to
 *	hardware events and software requests.  The hardware may have gone
 *	through a power-off reset, or it may have maintained state from the
 *	previous suspend() which the driver may rely on while resuming.  On most
 *	platforms, there are no restrictions on availability of resources like
 *	clocks during @resume().
 *
 * @freeze: Hibernation-specific, executed before creating a hibernation image.
 *	Quiesce operations so that a consistent image can be created, but do NOT
 *	otherwise put the device into a low power device state and do NOT emit
 *	system wakeup events.  Save in main memory the device settings to be
 *	used by @restore() during the subsequent resume from hibernation or by
 *	the subsequent @thaw(), if the creation of the image or the restoration
 *	of main memory contents from it fails.
 *
 * @thaw: Hibernation-specific, executed after creating a hibernation image OR
 *	if the creation of the image fails.  Also executed after a failing
 *	attempt to restore the contents of main memory from such an image.
 *	Undo the changes made by the preceding @freeze(), so the device can be
 *	operated in the same way as immediately before the call to @freeze().
 *
 * @poweroff: Hibernation-specific, executed after saving a hibernation image.
 *	Quiesce the device, put it into a low power state appropriate for the
 *	upcoming system state (such as PCI_D3hot), and enable wakeup events as
 *	appropriate.
 *
 * @restore: Hibernation-specific, executed after restoring the contents of main
 *	memory from a hibernation image.  Driver starts working again,
 *	responding to hardware events and software requests.  Drivers may NOT
 *	make ANY assumptions about the hardware state right prior to @restore().
 *	On most platforms, there are no restrictions on availability of
 *	resources like clocks during @restore().
 *
 * @suspend_noirq: Complete the operations of ->suspend() by carrying out any
 *	actions required for suspending the device that need interrupts to be
 *	disabled
 *
 * @resume_noirq: Prepare for the execution of ->resume() by carrying out any
 *	actions required for resuming the device that need interrupts to be
 *	disabled
 *
 * @freeze_noirq: Complete the operations of ->freeze() by carrying out any
 *	actions required for freezing the device that need interrupts to be
 *	disabled
 *
 * @thaw_noirq: Prepare for the execution of ->thaw() by carrying out any
 *	actions required for thawing the device that need interrupts to be
 *	disabled
 *
 * @poweroff_noirq: Complete the operations of ->poweroff() by carrying out any
 *	actions required for handling the device that need interrupts to be
 *	disabled
 *
 * @restore_noirq: Prepare for the execution of ->restore() by carrying out any
 *	actions required for restoring the operations of the device that need
 *	interrupts to be disabled
 *
 * All of the above callbacks, except for @complete(), return error codes.
 * However, the error codes returned by the resume operations, @resume(),
 * @thaw(), @restore(), @resume_noirq(), @thaw_noirq(), and @restore_noirq() do
 * not cause the PM core to abort the resume transition during which they are
 * returned.  The error codes returned in that cases are only printed by the PM
 * core to the system logs for debugging purposes.  Still, it is recommended
 * that drivers only return error codes from their resume methods in case of an
 * unrecoverable failure (i.e. when the device being handled refuses to resume
 * and becomes unusable) to allow us to modify the PM core in the future, so
 * that it can avoid attempting to handle devices that failed to resume and
 * their children.
 *
 * It is allowed to unregister devices while the above callbacks are being
 * executed.  However, it is not allowed to unregister a device from within any
 * of its own callbacks.
 *
 * There also are the following callbacks related to run-time power management
 * of devices:
 *
 * @runtime_suspend: Prepare the device for a condition in which it won't be
 *	able to communicate with the CPU(s) and RAM due to power management.
 *	This need not mean that the device should be put into a low power state.
 *	For example, if the device is behind a link which is about to be turned
 *	off, the device may remain at full power.  If the device does go to low
 *	power and is capable of generating run-time wake-up events, remote
 *	wake-up (i.e., a hardware mechanism allowing the device to request a
 *	change of its power state via a wake-up event, such as PCI PME) should
 *	be enabled for it.
 *
 * @runtime_resume: Put the device into the fully active state in response to a
 *	wake-up event generated by hardware or at the request of software.  If
 *	necessary, put the device into the full power state and restore its
 *	registers, so that it is fully operational.
 *
 * @runtime_idle: Device appears to be inactive and it might be put into a low
 *	power state if all of the necessary conditions are satisfied.  Check
 *	these conditions and handle the device as appropriate, possibly queueing
 *	a suspend request for it.  The return value is ignored by the PM core.
 */

struct dev_pm_ops {
	int (*prepare)(struct device *dev);
	void (*complete)(struct device *dev);
	int (*suspend)(struct device *dev);
	int (*resume)(struct device *dev);
	int (*freeze)(struct device *dev);
	int (*thaw)(struct device *dev);
	int (*poweroff)(struct device *dev);
	int (*restore)(struct device *dev);
	int (*suspend_noirq)(struct device *dev);
	int (*resume_noirq)(struct device *dev);
	int (*freeze_noirq)(struct device *dev);
	int (*thaw_noirq)(struct device *dev);
	int (*poweroff_noirq)(struct device *dev);
	int (*restore_noirq)(struct device *dev);
	int (*runtime_suspend)(struct device *dev);
	int (*runtime_resume)(struct device *dev);
	int (*runtime_idle)(struct device *dev);
};

/*
 * Use this if you want to use the same suspend and resume callbacks for suspend
 * to RAM and hibernation.
 */










/**
 * PM_EVENT_ messages
 *
 * The following PM_EVENT_ messages are defined for the internal use of the PM
 * core, in order to provide a mechanism allowing the high level suspend and
 * hibernation code to convey the necessary information to the device PM core
 * code:
 *
 * ON		No transition.
 *
 * FREEZE 	System is going to hibernate, call ->prepare() and ->freeze()
 *		for all devices.
 *
 * SUSPEND	System is going to suspend, call ->prepare() and ->suspend()
 *		for all devices.
 *
 * HIBERNATE	Hibernation image has been saved, call ->prepare() and
 *		->poweroff() for all devices.
 *
 * QUIESCE	Contents of main memory are going to be restored from a (loaded)
 *		hibernation image, call ->prepare() and ->freeze() for all
 *		devices.
 *
 * RESUME	System is resuming, call ->resume() and ->complete() for all
 *		devices.
 *
 * THAW		Hibernation image has been created, call ->thaw() and
 *		->complete() for all devices.
 *
 * RESTORE	Contents of main memory have been restored from a hibernation
 *		image, call ->restore() and ->complete() for all devices.
 *
 * RECOVER	Creation of a hibernation image or restoration of the main
 *		memory contents from a hibernation image has failed, call
 *		->thaw() and ->complete() for all devices.
 *
 * The following PM_EVENT_ messages are defined for internal use by
 * kernel subsystems.  They are never issued by the PM core.
 *
 * USER_SUSPEND		Manual selective suspend was issued by userspace.
 *
 * USER_RESUME		Manual selective resume was issued by userspace.
 *
 * REMOTE_WAKEUP	Remote-wakeup request was received from the device.
 *
 * AUTO_SUSPEND		Automatic (device idle) runtime suspend was
 *			initiated by the subsystem.
 *
 * AUTO_RESUME		Automatic (device needed) runtime resume was
 *			requested by a driver.
 */









































/**
 * Device power management states
 *
 * These state labels are used internally by the PM core to indicate the current
 * status of a device with respect to the PM core operations.
 *
 * DPM_ON		Device is regarded as operational.  Set this way
 *			initially and when ->complete() is about to be called.
 *			Also set when ->prepare() fails.
 *
 * DPM_PREPARING	Device is going to be prepared for a PM transition.  Set
 *			when ->prepare() is about to be called.
 *
 * DPM_RESUMING		Device is going to be resumed.  Set when ->resume(),
 *			->thaw(), or ->restore() is about to be called.
 *
 * DPM_SUSPENDING	Device has been prepared for a power transition.  Set
 *			when ->prepare() has just succeeded.
 *
 * DPM_OFF		Device is regarded as inactive.  Set immediately after
 *			->suspend(), ->freeze(), or ->poweroff() has succeeded.
 *			Also set when ->resume()_noirq, ->thaw_noirq(), or
 *			->restore_noirq() is about to be called.
 *
 * DPM_OFF_IRQ		Device is in a "deep sleep".  Set immediately after
 *			->suspend_noirq(), ->freeze_noirq(), or
 *			->poweroff_noirq() has just succeeded.
 */

enum dpm_state {
	DPM_INVALID,
	DPM_ON,
	DPM_PREPARING,
	DPM_RESUMING,
	DPM_SUSPENDING,
	DPM_OFF,
	DPM_OFF_IRQ,
};

/**
 * Device run-time power management status.
 *
 * These status labels are used internally by the PM core to indicate the
 * current status of a device with respect to the PM core operations.  They do
 * not reflect the actual power state of the device or its status as seen by the
 * driver.
 *
 * RPM_ACTIVE		Device is fully operational.  Indicates that the device
 *			bus type's ->runtime_resume() callback has completed
 *			successfully.
 *
 * RPM_SUSPENDED	Device bus type's ->runtime_suspend() callback has
 *			completed successfully.  The device is regarded as
 *			suspended.
 *
 * RPM_RESUMING		Device bus type's ->runtime_resume() callback is being
 *			executed.
 *
 * RPM_SUSPENDING	Device bus type's ->runtime_suspend() callback is being
 *			executed.
 */

enum rpm_status {
	RPM_ACTIVE = 0,
	RPM_RESUMING,
	RPM_SUSPENDED,
	RPM_SUSPENDING,
};

/**
 * Device run-time power management request types.
 *
 * RPM_REQ_NONE		Do nothing.
 *
 * RPM_REQ_IDLE		Run the device bus type's ->runtime_idle() callback
 *
 * RPM_REQ_SUSPEND	Run the device bus type's ->runtime_suspend() callback
 *
 * RPM_REQ_RESUME	Run the device bus type's ->runtime_resume() callback
 */

enum rpm_request {
	RPM_REQ_NONE = 0,
	RPM_REQ_IDLE,
	RPM_REQ_SUSPEND,
	RPM_REQ_RESUME,
};

struct dev_pm_info {
	pm_message_t		power_state;
	unsigned int		can_wakeup:1;
	unsigned int		should_wakeup:1;
	enum dpm_state		status;		/* Owned by the PM core */
#if definedEx(CONFIG_PM_SLEEP)
	struct list_head	entry;
#endif
#if definedEx(CONFIG_PM_RUNTIME)
	struct timer_list	suspend_timer;
	unsigned long		timer_expires;
	struct work_struct	work;
	wait_queue_head_t	wait_queue;
	spinlock_t		lock;
	atomic_t		usage_count;
	atomic_t		child_count;
	unsigned int		disable_depth:3;
	unsigned int		ignore_children:1;
	unsigned int		idle_notification:1;
	unsigned int		request_pending:1;
	unsigned int		deferred_resume:1;
	unsigned int		run_wake:1;
	enum rpm_request	request;
	enum rpm_status		runtime_status;
	int			runtime_error;
#endif
};

/*
 * The PM_EVENT_ messages are also used by drivers implementing the legacy
 * suspend framework, based on the ->suspend() and ->resume() callbacks common
 * for suspend and hibernation transitions, according to the rules below.
 */

/* Necessary, because several drivers use PM_EVENT_PRETHAW */


/*
 * One transition is triggered by resume(), after a suspend() call; the
 * message is implicit:
 *
 * ON		Driver starts working again, responding to hardware events
 * 		and software requests.  The hardware may have gone through
 * 		a power-off reset, or it may have maintained state from the
 * 		previous suspend() which the driver will rely on while
 * 		resuming.  On most platforms, there are no restrictions on
 * 		availability of resources like clocks during resume().
 *
 * Other transitions are triggered by messages sent using suspend().  All
 * these transitions quiesce the driver, so that I/O queues are inactive.
 * That commonly entails turning off IRQs and DMA; there may be rules
 * about how to quiesce that are specific to the bus or the device's type.
 * (For example, network drivers mark the link state.)  Other details may
 * differ according to the message:
 *
 * SUSPEND	Quiesce, enter a low power device state appropriate for
 * 		the upcoming system state (such as PCI_D3hot), and enable
 * 		wakeup events as appropriate.
 *
 * HIBERNATE	Enter a low power device state appropriate for the hibernation
 * 		state (eg. ACPI S4) and enable wakeup events as appropriate.
 *
 * FREEZE	Quiesce operations so that a consistent image can be saved;
 * 		but do NOT otherwise enter a low power device state, and do
 * 		NOT emit system wakeup events.
 *
 * PRETHAW	Quiesce as if for FREEZE; additionally, prepare for restoring
 * 		the system from a snapshot taken after an earlier FREEZE.
 * 		Some drivers will need to reset their hardware state instead
 * 		of preserving it, to ensure that it's never mistaken for the
 * 		state which that earlier snapshot had set up.
 *
 * A minimally power-aware driver treats all messages as SUSPEND, fully
 * reinitializes its device during resume() -- whether or not it was reset
 * during the suspend/resume cycle -- and can't issue wakeup events.
 *
 * More power-aware drivers may also use low power states at runtime as
 * well as during system sleep states like PM_SUSPEND_STANDBY.  They may
 * be able to use wakeup events to exit from runtime low-power states,
 * or from system low-power states such as standby or suspend-to-RAM.
 */

#if definedEx(CONFIG_PM_SLEEP)
extern void device_pm_lock(void);
extern int sysdev_resume(void);
extern void dpm_resume_noirq(pm_message_t state);
extern void dpm_resume_end(pm_message_t state);

extern void device_pm_unlock(void);
extern int sysdev_suspend(pm_message_t state);
extern int dpm_suspend_noirq(pm_message_t state);
extern int dpm_suspend_start(pm_message_t state);

extern void __suspend_report_result(const char *function, void *fn, int ret);






#endif
#if !(definedEx(CONFIG_PM_SLEEP))



static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int dpm_suspend_start(pm_message_t state)
{
	return 0;
}



#endif
/* How to reorder dpm_list after device_move() */
enum dpm_order {
	DPM_ORDER_NONE,
	DPM_ORDER_DEV_AFTER_PARENT,
	DPM_ORDER_PARENT_BEFORE_DEV,
	DPM_ORDER_DEV_LAST,
};

/*
 * Global Power Management flags
 * Used to keep APM and ACPI from both being active
 */
extern unsigned int	pm_flags;





#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 2

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/alternative.h" 1





































 





	
	
	
	
	
	

	










					
					


 
					       
					       
































































		    
 
				  























#line 10 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/cpufeature.h" 1
/*
 * Defines x86 CPU feature bits
 */





























					  





























































































































































































































 























#line 11 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 1







































	

	

	





 









	
	
	
	

	

	
	
	
	
	
	
	
	
 	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	

	
	
	
	
	

	
	
	
	
	
	
	
	

	






























 







	
 	





















				

	
	
	    
	      
	      
	      
	    




	





	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


 
	
	
	
	
	
	
	
	
	
	













	


	

	





	

	


	









	





	
	
	
	
	
	
	

	
	

	
	



	
	
	
	
	
		
			
			
		
		
			
			
			
			
		
	
	
	

	
	

	
	

	

	
		
		
	




	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



	
	



	
	
	



	
	
	
	



	
	
	
	






	
	




	
		
		
	









 







	
	











	
	
	
	

	
 	
	
	
	
	


	


	

	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	

	
	
	
	
	

	
	
	




	

	
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
	
	




	
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
	








	

	
		      
		      
		      
		      
		      
		      
		      






	

	
	
		
		
	






	





 











			    

	














	

	
	
	
	




	

	
	
	
	



	






















			 
			 

	
	
	




			       
			       

	
	
	







	

	

	




	

	

	




	

	

	




	

	

	





	




	





	


	
		

		
	

		


		
			     



			     

	
	
		     




	
	
		     




	
	
	
		     

























	
	
	
		
	
		
			















    


	
		

	

    




	
	


	
		

	
	

	





	
		

	



					     


	
		

	
		     
		     























 









	
			  
			  
			  









	
			  
			  
			  




	






























































 




































					       



















	




	

	
	






				    

	
	
	

	
	
		

	



#line 12 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apicdef.h" 1



/*
 * Constants for various Intel APICs. (local APIC, IOAPIC, etc.)
 *
 * Alan Cox <Alan.Cox@linux.org>, 1995.
 * Ingo Molnar <mingo@redhat.com>, 1999, 2000
 */




/*
 * This is the IO-APIC register space as specified
 * by Intel docs:
 */











 




















































































































 


/*
 * All x86-64 systems are xAPIC compatible.
 * In the following, "apicid" is a physical APIC ID.
 */








/*
 * the local APIC register structure, memory mapped. Not terribly well
 * tested, but we might eventually use this one in the future - the
 * problem why we cannot use it right now is the P5 APIC, it has an
 * errata which cannot take 8-bit reads and writes, only 32-bit ones ...
 */


struct local_apic {

/*000*/	struct { unsigned int __reserved[4]; } __reserved_01;

/*010*/	struct { unsigned int __reserved[4]; } __reserved_02;

/*020*/	struct { /* APIC ID Register */
		unsigned int   __reserved_1	: 24,
			phys_apic_id	:  4,
			__reserved_2	:  4;
		unsigned int __reserved[3];
	} id;

/*030*/	const
	struct { /* APIC Version Register */
		unsigned int   version		:  8,
			__reserved_1	:  8,
			max_lvt		:  8,
			__reserved_2	:  8;
		unsigned int __reserved[3];
	} version;

/*040*/	struct { unsigned int __reserved[4]; } __reserved_03;

/*050*/	struct { unsigned int __reserved[4]; } __reserved_04;

/*060*/	struct { unsigned int __reserved[4]; } __reserved_05;

/*070*/	struct { unsigned int __reserved[4]; } __reserved_06;

/*080*/	struct { /* Task Priority Register */
		unsigned int   priority	:  8,
			__reserved_1	: 24;
		unsigned int __reserved_2[3];
	} tpr;

/*090*/	const
	struct { /* Arbitration Priority Register */
		unsigned int   priority	:  8,
			__reserved_1	: 24;
		unsigned int __reserved_2[3];
	} apr;

/*0A0*/	const
	struct { /* Processor Priority Register */
		unsigned int   priority	:  8,
			__reserved_1	: 24;
		unsigned int __reserved_2[3];
	} ppr;

/*0B0*/	struct { /* End Of Interrupt Register */
		unsigned int   eoi;
		unsigned int __reserved[3];
	} eoi;

/*0C0*/	struct { unsigned int __reserved[4]; } __reserved_07;

/*0D0*/	struct { /* Logical Destination Register */
		unsigned int   __reserved_1	: 24,
			logical_dest	:  8;
		unsigned int __reserved_2[3];
	} ldr;

/*0E0*/	struct { /* Destination Format Register */
		unsigned int   __reserved_1	: 28,
			model		:  4;
		unsigned int __reserved_2[3];
	} dfr;

/*0F0*/	struct { /* Spurious Interrupt Vector Register */
		unsigned int	spurious_vector	:  8,
			apic_enabled	:  1,
			focus_cpu	:  1,
			__reserved_2	: 22;
		unsigned int __reserved_3[3];
	} svr;

/*100*/	struct { /* In Service Register */
/*170*/		unsigned int bitfield;
		unsigned int __reserved[3];
	} isr [8];

/*180*/	struct { /* Trigger Mode Register */
/*1F0*/		unsigned int bitfield;
		unsigned int __reserved[3];
	} tmr [8];

/*200*/	struct { /* Interrupt Request Register */
/*270*/		unsigned int bitfield;
		unsigned int __reserved[3];
	} irr [8];

/*280*/	union { /* Error Status Register */
		struct {
			unsigned int   send_cs_error			:  1,
				receive_cs_error		:  1,
				send_accept_error		:  1,
				receive_accept_error		:  1,
				__reserved_1			:  1,
				send_illegal_vector		:  1,
				receive_illegal_vector		:  1,
				illegal_register_address	:  1,
				__reserved_2			: 24;
			unsigned int __reserved_3[3];
		} error_bits;
		struct {
			unsigned int errors;
			unsigned int __reserved_3[3];
		} all_errors;
	} esr;

/*290*/	struct { unsigned int __reserved[4]; } __reserved_08;

/*2A0*/	struct { unsigned int __reserved[4]; } __reserved_09;

/*2B0*/	struct { unsigned int __reserved[4]; } __reserved_10;

/*2C0*/	struct { unsigned int __reserved[4]; } __reserved_11;

/*2D0*/	struct { unsigned int __reserved[4]; } __reserved_12;

/*2E0*/	struct { unsigned int __reserved[4]; } __reserved_13;

/*2F0*/	struct { unsigned int __reserved[4]; } __reserved_14;

/*300*/	struct { /* Interrupt Command Register 1 */
		unsigned int   vector			:  8,
			delivery_mode		:  3,
			destination_mode	:  1,
			delivery_status		:  1,
			__reserved_1		:  1,
			level			:  1,
			trigger			:  1,
			__reserved_2		:  2,
			shorthand		:  2,
			__reserved_3		:  12;
		unsigned int __reserved_4[3];
	} icr1;

/*310*/	struct { /* Interrupt Command Register 2 */
		union {
			unsigned int   __reserved_1	: 24,
				phys_dest	:  4,
				__reserved_2	:  4;
			unsigned int   __reserved_3	: 24,
				logical_dest	:  8;
		} dest;
		unsigned int __reserved_4[3];
	} icr2;

/*320*/	struct { /* LVT - Timer */
		unsigned int   vector		:  8,
			__reserved_1	:  4,
			delivery_status	:  1,
			__reserved_2	:  3,
			mask		:  1,
			timer_mode	:  1,
			__reserved_3	: 14;
		unsigned int __reserved_4[3];
	} lvt_timer;

/*330*/	struct { /* LVT - Thermal Sensor */
		unsigned int  vector		:  8,
			delivery_mode	:  3,
			__reserved_1	:  1,
			delivery_status	:  1,
			__reserved_2	:  3,
			mask		:  1,
			__reserved_3	: 15;
		unsigned int __reserved_4[3];
	} lvt_thermal;

/*340*/	struct { /* LVT - Performance Counter */
		unsigned int   vector		:  8,
			delivery_mode	:  3,
			__reserved_1	:  1,
			delivery_status	:  1,
			__reserved_2	:  3,
			mask		:  1,
			__reserved_3	: 15;
		unsigned int __reserved_4[3];
	} lvt_pc;

/*350*/	struct { /* LVT - LINT0 */
		unsigned int   vector		:  8,
			delivery_mode	:  3,
			__reserved_1	:  1,
			delivery_status	:  1,
			polarity	:  1,
			remote_irr	:  1,
			trigger		:  1,
			mask		:  1,
			__reserved_2	: 15;
		unsigned int __reserved_3[3];
	} lvt_lint0;

/*360*/	struct { /* LVT - LINT1 */
		unsigned int   vector		:  8,
			delivery_mode	:  3,
			__reserved_1	:  1,
			delivery_status	:  1,
			polarity	:  1,
			remote_irr	:  1,
			trigger		:  1,
			mask		:  1,
			__reserved_2	: 15;
		unsigned int __reserved_3[3];
	} lvt_lint1;

/*370*/	struct { /* LVT - Error */
		unsigned int   vector		:  8,
			__reserved_1	:  4,
			delivery_status	:  1,
			__reserved_2	:  3,
			mask		:  1,
			__reserved_3	: 15;
		unsigned int __reserved_4[3];
	} lvt_error;

/*380*/	struct { /* Timer Initial Count Register */
		unsigned int   initial_count;
		unsigned int __reserved_2[3];
	} timer_icr;

/*390*/	const
	struct { /* Timer Current Count Register */
		unsigned int   curr_count;
		unsigned int __reserved_2[3];
	} timer_ccr;

/*3A0*/	struct { unsigned int __reserved[4]; } __reserved_16;

/*3B0*/	struct { unsigned int __reserved[4]; } __reserved_17;

/*3C0*/	struct { unsigned int __reserved[4]; } __reserved_18;

/*3D0*/	struct { unsigned int __reserved[4]; } __reserved_19;

/*3E0*/	struct { /* Timer Divide Configuration Register */
		unsigned int   divisor		:  4,
			__reserved_1	: 28;
		unsigned int __reserved_2[3];
	} timer_dcr;

/*3F0*/	struct { unsigned int __reserved[4]; } __reserved_20;

} __attribute__ ((packed));




 
  


#line 13 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/atomic.h" 1

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/atomic_32.h" 1























	











	











	
		     
		     











	
		     
		     













	

	
		     
		     
	










	
		     










	
		     












	

	
		     
		     
	












	

	
		     
		     
	













	

	
		     
		     
	











	

	
	
		

	
	
	
		     
		     
	



	
	
	
	
	












	




	




	













	
	
	
		
			
		
		
			
		
	
	

























	

































	

	






	
		
		
		
			
			
		

	


































































































#line 4 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/atomic.h" 2
 

#line 14 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/fixmap.h" 1
/*
 * fixmap.h: compile-time virtual memory allocation
 *
 * This file is subject to the terms and conditions of the GNU General Public
 * License.  See the file "COPYING" in the main directory of this archive
 * for more details.
 *
 * Copyright (C) 1998 Ingo Molnar
 *
 * Support of BIGMEM added by Gerhard Wichert, Siemens AG, July 1999
 * x86_32 and x86_64 integration by Gustavo F. Padovan, February 2009
 */





#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/kernel.h" 1





























































 
























































 


  












   











 

	





	




	

	









	

	

	

	

	

	

	



	

	







































	

	




				   
















 
	


	

















	





	




	
		

















	
	
	
	
	
	

















	
	
	


				
				

				
				

			







	
	
	


























 






 


 
















 





















 




























 






















































	



	



























 











	




	

































































































































	
	
	
	
	
	
	
	
	
	
	
	
	
	

























 






#line 20 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/fixmap.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/acpi.h" 1



/*
 *  Copyright (C) 2001 Paul Diefenbaugh <paul.s.diefenbaugh@intel.com>
 *  Copyright (C) 2001 Patrick Mochel <mochel@osdl.org>
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/acpi/pdc_intel.h" 1

/* _PDC bit definition for Intel processors */

































#line 28 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/acpi.h" 2

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/numa.h" 1

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/numa_32.h" 1



extern int pxm_to_nid(int pxm);
extern void numa_remove_cpu(int cpu);


extern void set_highmem_pages_init(void);
 




#line 4 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/numa.h" 2
 

#line 30 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/acpi.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/processor.h" 1







































	

	

	





 









	
	
	
	

	

	
	
	
	
	
	
	
	
 	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	

	
	
	
	
	

	
	
	
	
	
	
	
	

	






























 







	
 	





















				

	
	
	    
	      
	      
	      
	    




	





	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


 
	
	
	
	
	
	
	
	
	
	













	


	

	





	

	


	









	





	
	
	
	
	
	
	

	
	

	
	



	
	
	
	
	
		
			
			
		
		
			
			
			
			
		
	
	
	

	
	

	
	

	

	
		
		
	




	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



	
	



	
	
	



	
	
	
	



	
	
	
	






	
	




	
		
		
	









 







	
	











	
	
	
	

	
 	
	
	
	
	


	


	

	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	

	
	
	
	
	

	
	
	




	

	
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
	
	




	
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
		
	
		
	








	

	
		      
		      
		      
		      
		      
		      
		      






	

	
	
		
		
	






	





 











			    

	














	

	
	
	
	




	

	
	
	
	



	






















			 
			 

	
	
	




			       
			       

	
	
	







	

	

	




	

	

	




	

	

	




	

	

	





	




	





	


	
		

		
	

		


		
			     



			     

	
	
		     




	
	
		     




	
	
	
		     

























	
	
	
		
	
		
			















    


	
		

	

    




	
	


	
		

	
	

	





	
		

	



					     


	
		

	
		     
		     























 









	
			  
			  
			  









	
			  
			  
			  




	






























































 




































					       



















	




	

	
	






				    

	
	
	

	
	
		

	



#line 31 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/acpi.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mmu.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/spinlock.h" 1






















































































 


  
				   







 






 

 













 

 
 
 

	





	




	




	





















 















 





 




















































 







	










	




	




	














	














	




	




	




	




	




	









	




	




	




	




	




















#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mmu.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mutex.h" 1
/*
 * Mutexes: blocking mutual exclusion locks
 *
 * started by Ingo Molnar:
 *
 *  Copyright (C) 2004, 2005, 2006 Red Hat, Inc., Ingo Molnar <mingo@redhat.com>
 *
 * This file contains the main data structure and API definitions.
 */







































	
	
	
	

	


	
	


	








	
	

	





 











 












			 









	









					

					




 


















#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mmu.h" 2

/*
 * The x86 doesn't have a mmu context, but
 * we put the segment information here.
 */
typedef struct {
	void *ldt;
	int size;
	struct mutex lock;
	void *vdso;
} mm_context_t;

#if definedEx(CONFIG_SMP)
void leave_mm(int cpu);
#endif
#if !(definedEx(CONFIG_SMP))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void leave_mm(int cpu)
{
}
#endif

#line 32 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/acpi.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mpspec.h" 1


















 













 
















 



	




	




	









 



 











				   



				 





 

	





	







































	




	
	












	
	












#line 33 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/acpi.h" 2




/*
 * Calling conventions:
 *
 * ACPI_SYSTEM_XFACE        - Interfaces to host OS (handlers, threads)
 * ACPI_EXTERNAL_XFACE      - External ACPI interfaces
 * ACPI_INTERNAL_XFACE      - Internal ACPI interfaces
 * ACPI_INTERNAL_VAR_XFACE  - Internal variable-parameter list interfaces
 */





/* Asm macros */







int __acpi_acquire_global_lock(unsigned int *lock);
int __acpi_release_global_lock(unsigned int *lock);







/*
 * Math helper asm macros
 */













#if definedEx(CONFIG_ACPI)
extern int acpi_lapic;
extern int acpi_ioapic;
extern int acpi_noirq;
extern int acpi_strict;
extern int acpi_disabled;
extern int acpi_ht;
extern int acpi_pci_disabled;
extern int acpi_skip_timer_override;
extern int acpi_use_timer_override;

extern u8 acpi_sci_flags;
extern int acpi_sci_override_gsi;
void acpi_pic_sci_set_trigger(unsigned int, u16);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void disable_acpi(void)
{
	acpi_disabled = 1;
	acpi_ht = 0;
	acpi_pci_disabled = 1;
	acpi_noirq = 1;
}

extern int acpi_gsi_to_irq(u32 gsi, unsigned int *irq);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void acpi_noirq_set(void) { acpi_noirq = 1; }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void acpi_disable_pci(void)
{
	acpi_pci_disabled = 1;
	acpi_noirq_set();
}

/* routines for saving/restoring kernel state */
extern int acpi_save_state_mem(void);
extern void acpi_restore_state_mem(void);

extern unsigned long acpi_wakeup_address;

/* early initialization routine */
extern void acpi_reserve_wakeup_memory(void);

/*
 * Check if the CPU can handle C2 and deeper
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned int acpi_processor_cstate_check(unsigned int max_cstate)
{
	/*
	 * Early models (<=5) of AMD Opterons are not supposed to go into
	 * C2 state.
	 *
	 * Steppings 0x0A and later are good
	 */
	if (boot_cpu_data.x86 == 0x0F &&
	    boot_cpu_data.x86_vendor == 2 &&
	    boot_cpu_data.x86_model <= 0x05 &&
	    boot_cpu_data.x86_mask < 0x0A)
		return 1;
	else if ((__builtin_constant_p((3*32+21)) && ( ((((3*32+21))>>5)==0 && (1UL<<(((3*32+21))&31) & (
#if (!(definedEx(CONFIG_MATH_EMULATION)) && !(definedEx(CONFIG_MATH_EMULATION)))
(1<<((0*32+ 0) & 31))
#endif
#if (definedEx(CONFIG_MATH_EMULATION) && !((!(definedEx(CONFIG_MATH_EMULATION)) && !(definedEx(CONFIG_MATH_EMULATION)))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+ 3)) & 31)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+ 5) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if ((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE)) && (definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64)))
(1<<((0*32+ 6) & 31))
#endif
#if (!((definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64))) && !(((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE)) && (definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64)))))
0
#endif
| (1<<((0*32+ 8) & 31))|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+13)) & 31)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+24) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|(1<<((0*32+15) & 31))| 
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+25) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+26) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
))) || ((((3*32+21))>>5)==1 && (1UL<<(((3*32+21))&31) & (
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((1*32+29) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_X86_USE_3DNOW))
(1<<((1*32+31) & 31))
#endif
#if (!(definedEx(CONFIG_X86_USE_3DNOW)) && !((definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_X86_USE_3DNOW))))
0
#endif
))) || ((((3*32+21))>>5)==2 && (1UL<<(((3*32+21))&31) & 0)) || ((((3*32+21))>>5)==3 && (1UL<<(((3*32+21))&31) & (
#if ((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_P6_NOP)) && (definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64)))
(1<<((3*32+20) & 31))
#endif
#if (!((definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64))) && !(((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_P6_NOP)) && (definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64)))))
0
#endif
))) || ((((3*32+21))>>5)==4 && (1UL<<(((3*32+21))&31) & 0)) || ((((3*32+21))>>5)==5 && (1UL<<(((3*32+21))&31) & 0)) || ((((3*32+21))>>5)==6 && (1UL<<(((3*32+21))&31) & 0)) || ((((3*32+21))>>5)==7 && (1UL<<(((3*32+21))&31) & 0)) ) ? 1 : (__builtin_constant_p(((3*32+21))) ? constant_test_bit(((3*32+21)), ((unsigned long *)((&boot_cpu_data)->x86_capability))) : variable_test_bit(((3*32+21)), ((unsigned long *)((&boot_cpu_data)->x86_capability))))))
		return 1;
	else
		return max_cstate;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 bool arch_has_acpi_pdc(void)
{
	struct cpuinfo_x86 *c = &
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
(*({ unsigned long __ptr; __asm__ ("" : "=r"(__ptr) : "0"((&per_cpu__cpu_info))); (typeof((&per_cpu__cpu_info))) (__ptr + (((__per_cpu_offset[0])))); }))
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
boot_cpu_data
#endif
;
	return (c->x86_vendor == 0 ||
		c->x86_vendor == 5);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void arch_acpi_set_pdc_bits(u32 *buf)
{
	struct cpuinfo_x86 *c = &
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
(*({ unsigned long __ptr; __asm__ ("" : "=r"(__ptr) : "0"((&per_cpu__cpu_info))); (typeof((&per_cpu__cpu_info))) (__ptr + (((__per_cpu_offset[0])))); }))
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
boot_cpu_data
#endif
;

	buf[2] |= ((0x0010) | (0x0008) | (0x0002) | (0x0100) | (0x0200));

	if ((__builtin_constant_p((4*32+ 7)) && ( ((((4*32+ 7))>>5)==0 && (1UL<<(((4*32+ 7))&31) & (
#if (!(definedEx(CONFIG_MATH_EMULATION)) && !(definedEx(CONFIG_MATH_EMULATION)))
(1<<((0*32+ 0) & 31))
#endif
#if (definedEx(CONFIG_MATH_EMULATION) && !((!(definedEx(CONFIG_MATH_EMULATION)) && !(definedEx(CONFIG_MATH_EMULATION)))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+ 3)) & 31)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+ 5) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if ((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE)) && (definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64)))
(1<<((0*32+ 6) & 31))
#endif
#if (!((definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64))) && !(((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE)) && (definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64)))))
0
#endif
| (1<<((0*32+ 8) & 31))|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+13)) & 31)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+24) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|(1<<((0*32+15) & 31))| 
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+25) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+26) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
))) || ((((4*32+ 7))>>5)==1 && (1UL<<(((4*32+ 7))&31) & (
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((1*32+29) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_X86_USE_3DNOW))
(1<<((1*32+31) & 31))
#endif
#if (!(definedEx(CONFIG_X86_USE_3DNOW)) && !((definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_X86_USE_3DNOW))))
0
#endif
))) || ((((4*32+ 7))>>5)==2 && (1UL<<(((4*32+ 7))&31) & 0)) || ((((4*32+ 7))>>5)==3 && (1UL<<(((4*32+ 7))&31) & (
#if ((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_P6_NOP)) && (definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64)))
(1<<((3*32+20) & 31))
#endif
#if (!((definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64))) && !(((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_P6_NOP)) && (definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64)))))
0
#endif
))) || ((((4*32+ 7))>>5)==4 && (1UL<<(((4*32+ 7))&31) & 0)) || ((((4*32+ 7))>>5)==5 && (1UL<<(((4*32+ 7))&31) & 0)) || ((((4*32+ 7))>>5)==6 && (1UL<<(((4*32+ 7))&31) & 0)) || ((((4*32+ 7))>>5)==7 && (1UL<<(((4*32+ 7))&31) & 0)) ) ? 1 : (__builtin_constant_p(((4*32+ 7))) ? constant_test_bit(((4*32+ 7)), ((unsigned long *)((c)->x86_capability))) : variable_test_bit(((4*32+ 7)), ((unsigned long *)((c)->x86_capability))))))
		buf[2] |= ((0x0008) | (0x0002) | (0x0020) | (0x0800) | (0x0001));

	if ((__builtin_constant_p((0*32+22)) && ( ((((0*32+22))>>5)==0 && (1UL<<(((0*32+22))&31) & (
#if (!(definedEx(CONFIG_MATH_EMULATION)) && !(definedEx(CONFIG_MATH_EMULATION)))
(1<<((0*32+ 0) & 31))
#endif
#if (definedEx(CONFIG_MATH_EMULATION) && !((!(definedEx(CONFIG_MATH_EMULATION)) && !(definedEx(CONFIG_MATH_EMULATION)))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+ 3)) & 31)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+ 5) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if ((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE)) && (definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64)))
(1<<((0*32+ 6) & 31))
#endif
#if (!((definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64))) && !(((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE)) && (definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64)))))
0
#endif
| (1<<((0*32+ 8) & 31))|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+13)) & 31)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+24) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|(1<<((0*32+15) & 31))| 
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+25) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+26) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
))) || ((((0*32+22))>>5)==1 && (1UL<<(((0*32+22))&31) & (
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((1*32+29) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_X86_USE_3DNOW))
(1<<((1*32+31) & 31))
#endif
#if (!(definedEx(CONFIG_X86_USE_3DNOW)) && !((definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_X86_USE_3DNOW))))
0
#endif
))) || ((((0*32+22))>>5)==2 && (1UL<<(((0*32+22))&31) & 0)) || ((((0*32+22))>>5)==3 && (1UL<<(((0*32+22))&31) & (
#if ((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_P6_NOP)) && (definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64)))
(1<<((3*32+20) & 31))
#endif
#if (!((definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64))) && !(((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_P6_NOP)) && (definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64)))))
0
#endif
))) || ((((0*32+22))>>5)==4 && (1UL<<(((0*32+22))&31) & 0)) || ((((0*32+22))>>5)==5 && (1UL<<(((0*32+22))&31) & 0)) || ((((0*32+22))>>5)==6 && (1UL<<(((0*32+22))&31) & 0)) || ((((0*32+22))>>5)==7 && (1UL<<(((0*32+22))&31) & 0)) ) ? 1 : (__builtin_constant_p(((0*32+22))) ? constant_test_bit(((0*32+22)), ((unsigned long *)((c)->x86_capability))) : variable_test_bit(((0*32+22)), ((unsigned long *)((c)->x86_capability))))))
		buf[2] |= (0x0004);

	/*
	 * If mwait/monitor is unsupported, C2/C3_FFH will be disabled
	 */
	if (!(__builtin_constant_p((4*32+ 3)) && ( ((((4*32+ 3))>>5)==0 && (1UL<<(((4*32+ 3))&31) & (
#if (!(definedEx(CONFIG_MATH_EMULATION)) && !(definedEx(CONFIG_MATH_EMULATION)))
(1<<((0*32+ 0) & 31))
#endif
#if (definedEx(CONFIG_MATH_EMULATION) && !((!(definedEx(CONFIG_MATH_EMULATION)) && !(definedEx(CONFIG_MATH_EMULATION)))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+ 3)) & 31)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+ 5) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if ((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE)) && (definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64)))
(1<<((0*32+ 6) & 31))
#endif
#if (!((definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64))) && !(((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE)) && (definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64)))))
0
#endif
| (1<<((0*32+ 8) & 31))|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+13)) & 31)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+24) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|(1<<((0*32+15) & 31))| 
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+25) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+26) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
))) || ((((4*32+ 3))>>5)==1 && (1UL<<(((4*32+ 3))&31) & (
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((1*32+29) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_X86_USE_3DNOW))
(1<<((1*32+31) & 31))
#endif
#if (!(definedEx(CONFIG_X86_USE_3DNOW)) && !((definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_X86_USE_3DNOW))))
0
#endif
))) || ((((4*32+ 3))>>5)==2 && (1UL<<(((4*32+ 3))&31) & 0)) || ((((4*32+ 3))>>5)==3 && (1UL<<(((4*32+ 3))&31) & (
#if ((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_P6_NOP)) && (definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64)))
(1<<((3*32+20) & 31))
#endif
#if (!((definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64))) && !(((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_P6_NOP)) && (definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64)))))
0
#endif
))) || ((((4*32+ 3))>>5)==4 && (1UL<<(((4*32+ 3))&31) & 0)) || ((((4*32+ 3))>>5)==5 && (1UL<<(((4*32+ 3))&31) & 0)) || ((((4*32+ 3))>>5)==6 && (1UL<<(((4*32+ 3))&31) & 0)) || ((((4*32+ 3))>>5)==7 && (1UL<<(((4*32+ 3))&31) & 0)) ) ? 1 : (__builtin_constant_p(((4*32+ 3))) ? constant_test_bit(((4*32+ 3)), ((unsigned long *)((c)->x86_capability))) : variable_test_bit(((4*32+ 3)), ((unsigned long *)((c)->x86_capability))))))
		buf[2] &= ~((0x0200));
}

#endif
#if !(definedEx(CONFIG_ACPI))


static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void acpi_noirq_set(void) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void acpi_disable_pci(void) { }
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void disable_acpi(void) { }

#endif


struct bootnode;

#if definedEx(CONFIG_ACPI_NUMA)
extern int acpi_numa;
extern int acpi_get_nodes(struct bootnode *physnodes);
extern int acpi_scan_nodes(unsigned long start, unsigned long end);

extern void acpi_fake_nodes(const struct bootnode *fake_nodes,
				   int num_nodes);
#endif
#if !(definedEx(CONFIG_ACPI_NUMA))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void acpi_fake_nodes(const struct bootnode *fake_nodes,
				   int num_nodes)
{
}
#endif



#line 21 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/fixmap.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apicdef.h" 1




























 




















































































































 
























	

	

	
		
			
			
		
	

	
	
		
			
			
			
		
	

	

	

	

	

	
		
			
		
	

	
	
		
			
		
	

	
	
		
			
		
	

	
		
		
	

	

	
		
			
		
	

	
		
			
		
	

	
		
			
			
			
		
	

	
		
		
	

	
		
		
	

	
		
		
	

	
		
			
				
				
				
				
				
				
				
				
			
		
		
			
			
		
	

	

	

	

	

	

	

	

	
		
			
			
			
			
			
			
			
			
			
		
	

	
		
			
				
				
			
				
		
		
	

	
		
			
			
			
			
			
			
		
	

	
		
			
			
			
			
			
			
		
	

	
		
			
			
			
			
			
			
		
	

	
		
			
			
			
			
			
			
			
			
		
	

	
		
			
			
			
			
			
			
			
			
		
	

	
		
			
			
			
			
			
		
	

	
		
		
	

	
	
		
		
	

	

	

	

	

	
		
			
		
	

	






 
  


#line 22 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/fixmap.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/page.h" 1










 





				   

	



				  

	


































#line 23 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/fixmap.h" 2

#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/threads.h" 1



































#line 25 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/fixmap.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/kmap_types.h" 1



#if definedEx(CONFIG_DEBUG_HIGHMEM)

#endif
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/asm-generic/kmap_types.h" 1



#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM))

#endif
#if !((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC)))

#endif
enum km_type {

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_0 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_BOUNCE_READ,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_1 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_SKB_SUNRPC_DATA,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_2 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_SKB_DATA_SOFTIRQ,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_3 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_USER0,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_4 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_USER1,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_5 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_BIO_SRC_IRQ,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_6 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_BIO_DST_IRQ,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_7 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_PTE0,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_8 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_PTE1,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_9 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_IRQ0,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_10 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_IRQ1,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_11 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_SOFTIRQ0,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_12 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_SOFTIRQ1,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_13 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_SYNC_ICACHE,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_14 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_SYNC_DCACHE,
/* UML specific, for copy_*_user - used in do_op_one_page */

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_15 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_UML_USERCOPY,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_16 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_IRQ_PTE,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_17 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_NMI,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_18 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_NMI_PTE,

#if (definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))
__KM_FENCE_19 ,
#endif
#if (!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && !((!((definedEx(CONFIG_DEBUG_HIGHMEM) && definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_X86_LOCAL_APIC))) && definedEx(CONFIG_X86_LOCAL_APIC) && definedEx(CONFIG_NEED_MULTIPLE_NODES))))))

#endif
	KM_TYPE_NR
};




#line 10 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/kmap_types.h" 2




#line 26 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/fixmap.h" 2
 

/*
 * We can't declare FIXADDR_TOP as variable for x86_64 because vsyscall
 * uses fixmaps that relies on FIXADDR_TOP for proper address calculation.
 * Because of this, FIXADDR_TOP x86 integration was left as later work.
 */

/* used by vmalloc.c, vsyscall.lds.S.
 *
 * Leave one empty page between vmalloc'ed areas and
 * the start of the fixmap.
 */
extern unsigned long __FIXADDR_TOP;




 





/*
 * Here we define all the compile-time 'special' virtual
 * addresses. The point is to have a constant address at
 * compile time, but to set the physical address only
 * in the boot process.
 * for x86_32: We allocate these special addresses
 * from the end of virtual memory (0xfffff000) backwards.
 * Also this lets us do fail-safe vmalloc(), we
 * can guarantee that these special addresses and
 * vmalloc()-ed addresses never overlap.
 *
 * These 'compile-time allocated' memory buffers are
 * fixed-size 4k pages (or larger if used with an increment
 * higher than 1). Use set_fixmap(idx,phys) to associate
 * physical memory with fixmap indices.
 *
 * TLB entries of such buffers will not be flushed across
 * task switches.
 */
enum fixed_addresses {

	FIX_HOLE,
	FIX_VDSO,
 	
	
			    
	

	FIX_DBGP_BASE,
	FIX_EARLYCON_MEM_BASE,
#if definedEx(CONFIG_PROVIDE_OHCI1394_DMA_INIT)
	FIX_OHCI1394_BASE,
#endif
#if definedEx(CONFIG_X86_LOCAL_APIC)
	FIX_APIC_BASE,	/* local (CPU) APIC) -- required for SMP or not */
#endif
#if definedEx(CONFIG_X86_IO_APIC)
	FIX_IO_APIC_BASE_0,
	FIX_IO_APIC_BASE_END = FIX_IO_APIC_BASE_0 + 64 - 1,
#endif
#if definedEx(CONFIG_X86_VISWS_APIC)
	FIX_CO_CPU,	/* Cobalt timer */
	FIX_CO_APIC,	/* Cobalt APIC Redirection Table */
	FIX_LI_PCIA,	/* Lithium PCI Bridge A */
	FIX_LI_PCIB,	/* Lithium PCI Bridge B */
#endif
#if definedEx(CONFIG_X86_F00F_BUG)
	FIX_F00F_IDT,	/* Virtual mapping for IDT */
#endif
#if definedEx(CONFIG_X86_CYCLONE_TIMER)
	FIX_CYCLONE_TIMER, /*cyclone timer register*/
#endif

	FIX_KMAP_BEGIN,	/* reserved pte's for temporary kernel mappings */
	FIX_KMAP_END = FIX_KMAP_BEGIN+(KM_TYPE_NR*1)-1,
#if definedEx(CONFIG_PCI_MMCONFIG)
	FIX_PCIE_MCFG,
#endif


	

	FIX_TEXT_POKE1,	/* reserve 2 pages for text_poke() */
	FIX_TEXT_POKE0, /* first page is last, because allocation is backward */
	__end_of_permanent_fixed_addresses,
	/*
	 * 256 temporary boot-time mappings, used by early_ioremap(),
	 * before ioremap() is functional.
	 *
	 * We round it up to the next 256 pages boundary so that we
	 * can have a single pgd entry and a single pte table:
	 */


	FIX_BTMAP_END = __end_of_permanent_fixed_addresses + 256 -
			(__end_of_permanent_fixed_addresses & 255),
	FIX_BTMAP_BEGIN = FIX_BTMAP_END + 64*4 - 1,

	FIX_WP_TEST,

#if definedEx(CONFIG_INTEL_TXT)
	FIX_TBOOT_BASE,
#endif
	__end_of_fixed_addresses
};


extern void reserve_top_address(unsigned long reserve);






extern int fixmaps_set;

extern pte_t *kmap_pte;
extern pgprot_t kmap_prot;
extern pte_t *pkmap_page_table;

void __native_set_fixmap(enum fixed_addresses idx, pte_t pte);
void native_set_fixmap(enum fixed_addresses idx,
		       phys_addr_t phys, pgprot_t flags);


static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __set_fixmap(enum fixed_addresses idx,
				phys_addr_t phys, pgprot_t flags)
{
	native_set_fixmap(idx, phys, flags);
}




/*
 * Some hardware wants to get fixmapped without caching.
 */









extern void __this_fixmap_does_not_exist(void);

/*
 * 'index to address' translation. If anyone tries to use the idx
 * directly without translation, we catch the bug with a NULL-deference
 * kernel oops. Illegal ranges of incoming indices are caught too.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 __attribute__((always_inline)) unsigned long fix_to_virt(const unsigned int idx)
{
	/*
	 * this branch gets completely eliminated after inlining,
	 * except when someone tries to use fixaddr indices in an
	 * illegal way. (such as mixing up address types or using
	 * out-of-range indices).
	 *
	 * If it doesn't get removed, the linker will complain
	 * loudly with a reasonably clear error message..
	 */
	if (idx >= __end_of_fixed_addresses)
		__this_fixmap_does_not_exist();

	return (((unsigned long)__FIXADDR_TOP) - ((idx) << 12));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long virt_to_fix(const unsigned long vaddr)
{
	do { if (__builtin_expect(!!(vaddr >=((unsigned long)__FIXADDR_TOP) || vaddr <(((unsigned long)__FIXADDR_TOP) -(__end_of_permanent_fixed_addresses << 12))), 0)) do { asm volatile("1:\tud2\n" ".pushsection __bug_table,\"a\"\n" "2:\t.long 1b, %c0\n" "\t.word %c1, 0\n" "\t.org 2b+%c2\n" ".popsection" : : "i" ("/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/fixmap.h"), "i" (15), "i" (sizeof(struct bug_entry))); do { } while (1); } while (0); } while(0);
	return ((((unsigned long)__FIXADDR_TOP) - ((vaddr)&(~(((1UL) << 12)-1)))) >> 12);
}


#line 15 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mpspec.h" 1


















 













 
















 



	




	




	









 



 











				   



				 





 

	





	







































	




	
	












	
	












#line 16 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/system.h" 1















 



				


		      











 



























































 


















 



































































 








	
	
	




	













	
	
	




	




	
	
	




	




	
	
	




	




	
	
	




	
	


	
		     
		     
		     
 	

	




	





	
	
	




	




	




 























	





























 





























































 



 



 














	
	



#line 17 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/msr.h" 1


















	
		
			
			
		
		
	



	
	
	
	



	
	




	
	
		     
	













 






	

	
	



						      

	

	
		     
		     
		     
		     
		     
		     
		     
	



				    

	




					

	
	
		     
		     
		     
		     
		     
		     
		     
		       
		     
	









	

	

	




	

	
	




 















	











	














	

	
	




	
	

	
	

	

	

	




	

	
	
	
	

	




	




	












































 

	
	



	
	


				

       


				

       


				    

	



	



	



	





#line 18 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 2



/*
 * Debugging macros
 */




/*
 * Define the default level of output to be very little
 * This can be turned up by using apic=verbose for more
 * information and apic=debug for _lots_ of information.
 * apic_verbosity is defined in apic.c
 */






#if definedEx(CONFIG_X86_LOCAL_APIC)
extern void generic_apic_probe(void);
#endif




#if definedEx(CONFIG_X86_LOCAL_APIC)
extern unsigned int apic_verbosity;
extern int local_apic_timer_c2_ok;

extern int disable_apic;

#if definedEx(CONFIG_SMP)
extern void __inquire_remote_apic(int apicid);
#endif
#if !(definedEx(CONFIG_SMP))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __inquire_remote_apic(int apicid)
{
}
#endif
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void default_inquire_remote_apic(int apicid)
{
	if (apic_verbosity >= 2)
		__inquire_remote_apic(apicid);
}

/*
 * With 82489DX we can't rely on apic feature bit
 * retrieved via cpuid but still have to deal with
 * such an apic chip so we assume that SMP configuration
 * is found from MP table (64bit case uses ACPI mostly
 * which set smp presence flag as well so we are safe
 * to use this helper too).
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 bool apic_from_smp_config(void)
{
	return smp_found_config && !disable_apic;
}

/*
 * Basic functions accessing APICs.
 */



#if definedEx(CONFIG_X86_64)
extern int is_vsmp_box(void);
#endif
#if !(definedEx(CONFIG_X86_64))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int is_vsmp_box(void)
{
	return 0;
}
#endif
extern void xapic_wait_icr_idle(void);
extern u32 safe_xapic_wait_icr_idle(void);
extern void xapic_icr_write(u32, u32);
extern int setup_profiling_timer(unsigned int);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_apic_mem_write(u32 reg, u32 v)
{
	volatile u32 *addr = (volatile u32 *)((fix_to_virt(FIX_APIC_BASE)) + reg);

	asm volatile ("661:\n\t" "movl %0, %1" "\n662:\n" ".section .altinstructions,\"a\"\n" " " ".balign 4" " " "\n" " " ".long" " " "661b\n" " " ".long" " " "663f\n" "	 .byte " "(3*32+19)" "\n" "	 .byte 662b-661b\n" "	 .byte 664f-663f\n" "	 .byte 0xff + (664f-663f) - (662b-661b)\n" ".previous\n" ".section .altinstr_replacement, \"ax\"\n" "663:\n\t" "xchgl %0, %1" "\n664:\n" ".previous" : 
 "=r"(v), "=m"(*addr) : "i" (0),"0"(v), "m"(*addr));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 u32 native_apic_mem_read(u32 reg)
{
	return *((volatile u32 *)((fix_to_virt(FIX_APIC_BASE)) + reg));
}

extern void native_apic_wait_icr_idle(void);
extern u32 native_safe_apic_wait_icr_idle(void);
extern void native_apic_icr_write(u32 low, u32 id);
extern u64 native_apic_icr_read(void);

extern int x2apic_mode;

#if definedEx(CONFIG_X86_X2APIC)
/*
 * Make previous memory operations globally visible before
 * sending the IPI through x2apic wrmsr. We need a serializing instruction or
 * mfence for this.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void x2apic_wrmsr_fence(void)
{
	asm volatile("mfence" : : : "memory");
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_apic_msr_write(u32 reg, u32 v)
{
	if (reg == 0xE0 || reg == 0x20 || reg == 0xD0 ||
	    reg == 0x30)
		return;

	wrmsr(0x800 + (reg >> 4), v, 0);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 u32 native_apic_msr_read(u32 reg)
{
	u32 low, high;

	if (reg == 0xE0)
		return -1;

	do { u64 __val = native_read_msr((0x800 +(reg >> 4))); (low) = (u32)__val; (high) = (u32)(__val >> 32); } while (0);
	return low;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_x2apic_wait_icr_idle(void)
{
	/* no need to wait for icr idle in x2apic */
	return;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 u32 native_safe_x2apic_wait_icr_idle(void)
{
	/* no need to wait for icr idle in x2apic */
	return 0;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void native_x2apic_icr_write(u32 low, u32 id)
{
	native_write_msr((0x800 +(0x300 >> 4)), (u32)((u64)(((__u64) id) << 32 | low)), (u32)((u64)(((__u64) id) << 32 | low) >> 32));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 u64 native_x2apic_icr_read(void)
{
	unsigned long val;

	((val) = native_read_msr((0x800 +(0x300 >> 4))));
	return val;
}

extern int x2apic_phys;
extern void check_x2apic(void);
extern void enable_x2apic(void);
extern void x2apic_icr_write(u32 low, u32 id);
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int x2apic_enabled(void)
{
	int msr, msr2;

	if (!(__builtin_constant_p((4*32+21)) && ( ((((4*32+21))>>5)==0 && (1UL<<(((4*32+21))&31) & (
#if (!(definedEx(CONFIG_MATH_EMULATION)) && !(definedEx(CONFIG_MATH_EMULATION)))
(1<<((0*32+ 0) & 31))
#endif
#if (definedEx(CONFIG_MATH_EMULATION) && !((!(definedEx(CONFIG_MATH_EMULATION)) && !(definedEx(CONFIG_MATH_EMULATION)))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+ 3)) & 31)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+ 5) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if ((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE)) && (definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64)))
(1<<((0*32+ 6) & 31))
#endif
#if (!((definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64))) && !(((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE)) && (definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64)))))
0
#endif
| (1<<((0*32+ 8) & 31))|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+13)) & 31)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+24) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|(1<<((0*32+15) & 31))| 
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+25) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+26) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
))) || ((((4*32+21))>>5)==1 && (1UL<<(((4*32+21))&31) & (
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((1*32+29) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_X86_USE_3DNOW))
(1<<((1*32+31) & 31))
#endif
#if (!(definedEx(CONFIG_X86_USE_3DNOW)) && !((definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_X86_USE_3DNOW))))
0
#endif
))) || ((((4*32+21))>>5)==2 && (1UL<<(((4*32+21))&31) & 0)) || ((((4*32+21))>>5)==3 && (1UL<<(((4*32+21))&31) & (
#if ((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_P6_NOP)) && (definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64)))
(1<<((3*32+20) & 31))
#endif
#if (!((definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64))) && !(((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_P6_NOP)) && (definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64)))))
0
#endif
))) || ((((4*32+21))>>5)==4 && (1UL<<(((4*32+21))&31) & 0)) || ((((4*32+21))>>5)==5 && (1UL<<(((4*32+21))&31) & 0)) || ((((4*32+21))>>5)==6 && (1UL<<(((4*32+21))&31) & 0)) || ((((4*32+21))>>5)==7 && (1UL<<(((4*32+21))&31) & 0)) ) ? 1 : (__builtin_constant_p(((4*32+21))) ? constant_test_bit(((4*32+21)), ((unsigned long *)((&boot_cpu_data)->x86_capability))) : variable_test_bit(((4*32+21)), ((unsigned long *)((&boot_cpu_data)->x86_capability))))))
		return 0;

	do { u64 __val = native_read_msr((0x0000001b)); (msr) = (u32)__val; (msr2) = (u32)(__val >> 32); } while (0);
	if (msr & (1UL << 10))
		return 1;
	return 0;
}


static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void x2apic_force_phys(void)
{
	x2apic_phys = 1;
}
#endif
#if !(definedEx(CONFIG_X86_X2APIC))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void check_x2apic(void)
{
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void enable_x2apic(void)
{
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int x2apic_enabled(void)
{
	return 0;
}
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void x2apic_force_phys(void)
{
}



#endif
extern void enable_IR_x2apic(void);

extern int get_physical_broadcast(void);

extern void apic_disable(void);
extern int lapic_get_maxlvt(void);
extern void clear_local_APIC(void);
extern void connect_bsp_APIC(void);
extern void disconnect_bsp_APIC(int virt_wire_setup);
extern void disable_local_APIC(void);
extern void lapic_shutdown(void);
extern int verify_local_APIC(void);
extern void cache_APIC_registers(void);
extern void sync_Arb_IDs(void);
extern void init_bsp_APIC(void);
extern void setup_local_APIC(void);
extern void end_local_APIC_setup(void);
extern void init_apic_mappings(void);
extern void setup_boot_APIC_clock(void);
extern void setup_secondary_APIC_clock(void);
extern int APIC_init_uniprocessor(void);
extern void enable_NMI_through_LVT0(void);

/*
 * On 32bit this is mach-xxx local
 */
#if definedEx(CONFIG_X86_64)
extern void early_init_lapic_mapping(void);
extern int apic_is_clustered_box(void);
#endif
#if !(definedEx(CONFIG_X86_64))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int apic_is_clustered_box(void)
{
	return 0;
}
#endif
extern u8 setup_APIC_eilvt_mce(u8 vector, u8 msg_type, u8 mask);
extern u8 setup_APIC_eilvt_ibs(u8 vector, u8 msg_type, u8 mask);


#endif








#if definedEx(CONFIG_X86_64)

#endif
#if !(definedEx(CONFIG_X86_64))
#endif
/*
 * Copyright 2004 James Cleverdon, IBM.
 * Subject to the GNU Public License, v.2
 *
 * Generic APIC sub-arch data struct.
 *
 * Hacked for x86-64 by James Cleverdon from i386 architecture code by
 * Martin Bligh, Andi Kleen, James Bottomley, John Stultz, and
 * James Cleverdon.
 */
struct apic {
	char *name;

	int (*probe)(void);
	int (*acpi_madt_oem_check)(char *oem_id, char *oem_table_id);
	int (*apic_id_registered)(void);

	u32 irq_delivery_mode;
	u32 irq_dest_mode;

	const struct cpumask *(*target_cpus)(void);

	int disable_esr;

	int dest_logical;
	unsigned long (*check_apicid_used)(physid_mask_t *map, int apicid);
	unsigned long (*check_apicid_present)(int apicid);

	void (*vector_allocation_domain)(int cpu, struct cpumask *retmask);
	void (*init_apic_ldr)(void);

	void (*ioapic_phys_id_map)(physid_mask_t *phys_map, physid_mask_t *retmap);

	void (*setup_apic_routing)(void);
	int (*multi_timer_check)(int apic, int irq);
	int (*apicid_to_node)(int logical_apicid);
	int (*cpu_to_logical_apicid)(int cpu);
	int (*cpu_present_to_apicid)(int mps_cpu);
	void (*apicid_to_cpu_present)(int phys_apicid, physid_mask_t *retmap);
	void (*setup_portio_remap)(void);
	int (*check_phys_apicid_present)(int phys_apicid);
	void (*enable_apic_mode)(void);
	int (*phys_pkg_id)(int cpuid_apic, int index_msb);

	/*
	 * When one of the next two hooks returns 1 the apic
	 * is switched to this. Essentially they are additional
	 * probe functions:
	 */
	int (*mps_oem_check)(struct mpc_table *mpc, char *oem, char *productid);

	unsigned int (*get_apic_id)(unsigned long x);
	unsigned long (*set_apic_id)(unsigned int id);
	unsigned long apic_id_mask;

	unsigned int (*cpu_mask_to_apicid)(const struct cpumask *cpumask);
	unsigned int (*cpu_mask_to_apicid_and)(const struct cpumask *cpumask,
					       const struct cpumask *andmask);

	/* ipi */
	void (*send_IPI_mask)(const struct cpumask *mask, int vector);
	void (*send_IPI_mask_allbutself)(const struct cpumask *mask,
					 int vector);
	void (*send_IPI_allbutself)(int vector);
	void (*send_IPI_all)(int vector);
	void (*send_IPI_self)(int vector);

	/* wakeup_secondary_cpu */
	int (*wakeup_secondary_cpu)(int apicid, unsigned long start_eip);

	int trampoline_phys_low;
	int trampoline_phys_high;

	void (*wait_for_init_deassert)(atomic_t *deassert);
	void (*smp_callin_clear_local_apic)(void);
	void (*inquire_remote_apic)(int apicid);

	/* apic ops */
	u32 (*read)(u32 reg);
	void (*write)(u32 reg, u32 v);
	u64 (*icr_read)(void);
	void (*icr_write)(u32 low, u32 high);
	void (*wait_icr_idle)(void);
	u32 (*safe_wait_icr_idle)(void);
};

/*
 * Pointer to the local APIC driver in use on this system (there's
 * always just one such driver in use - the kernel decides via an
 * early probing process which one it picks - and then sticks to it):
 */
extern struct apic *apic;

/*
 * APIC functionality to boot other CPUs - only used on SMP:
 */
#if definedEx(CONFIG_SMP)
extern atomic_t init_deasserted;
extern int wakeup_secondary_cpu_via_nmi(int apicid, unsigned long start_eip);
#endif
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 u32 apic_read(u32 reg)
{
	return apic->read(reg);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void apic_write(u32 reg, u32 val)
{
	apic->write(reg, val);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 u64 apic_icr_read(void)
{
	return apic->icr_read();
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void apic_icr_write(u32 low, u32 high)
{
	apic->icr_write(low, high);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void apic_wait_icr_idle(void)
{
	apic->wait_icr_idle();
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 u32 safe_apic_wait_icr_idle(void)
{
	return apic->safe_wait_icr_idle();
}


static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void ack_APIC_irq(void)
{
#if definedEx(CONFIG_X86_LOCAL_APIC)
	/*
	 * ack_APIC_irq() actually gets compiled as a single instruction
	 * ... yummie.
	 */

	/* Docs say use 0 for future compatibility */
	apic_write(0xB0, 0);
#endif
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned default_get_apic_id(unsigned long x)
{
	unsigned int ver = ((apic_read(0x30)) & 0xFFu);

	if (((ver) >= 0x14) || (__builtin_constant_p((3*32+26)) && ( ((((3*32+26))>>5)==0 && (1UL<<(((3*32+26))&31) & (
#if (!(definedEx(CONFIG_MATH_EMULATION)) && !(definedEx(CONFIG_MATH_EMULATION)))
(1<<((0*32+ 0) & 31))
#endif
#if (definedEx(CONFIG_MATH_EMULATION) && !((!(definedEx(CONFIG_MATH_EMULATION)) && !(definedEx(CONFIG_MATH_EMULATION)))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+ 3)) & 31)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+ 5) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if ((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE)) && (definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64)))
(1<<((0*32+ 6) & 31))
#endif
#if (!((definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64))) && !(((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_PAE)) && (definedEx(CONFIG_X86_PAE) || definedEx(CONFIG_X86_64)))))
0
#endif
| (1<<((0*32+ 8) & 31))|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+13)) & 31)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+24) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|(1<<((0*32+15) & 31))| 
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+25) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((0*32+26) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
))) || ((((3*32+26))>>5)==1 && (1UL<<(((3*32+26))&31) & (
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
(1<<((1*32+29) & 31))
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
0
#endif
|
#if (definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_X86_USE_3DNOW))
(1<<((1*32+31) & 31))
#endif
#if (!(definedEx(CONFIG_X86_USE_3DNOW)) && !((definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_X86_USE_3DNOW))))
0
#endif
))) || ((((3*32+26))>>5)==2 && (1UL<<(((3*32+26))&31) & 0)) || ((((3*32+26))>>5)==3 && (1UL<<(((3*32+26))&31) & (
#if ((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_P6_NOP)) && (definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64)))
(1<<((3*32+20) & 31))
#endif
#if (!((definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64))) && !(((definedEx(CONFIG_X86_64) || definedEx(CONFIG_X86_P6_NOP)) && (definedEx(CONFIG_X86_P6_NOP) || definedEx(CONFIG_X86_64)))))
0
#endif
))) || ((((3*32+26))>>5)==4 && (1UL<<(((3*32+26))&31) & 0)) || ((((3*32+26))>>5)==5 && (1UL<<(((3*32+26))&31) & 0)) || ((((3*32+26))>>5)==6 && (1UL<<(((3*32+26))&31) & 0)) || ((((3*32+26))>>5)==7 && (1UL<<(((3*32+26))&31) & 0)) ) ? 1 : (__builtin_constant_p(((3*32+26))) ? constant_test_bit(((3*32+26)), ((unsigned long *)((&boot_cpu_data)->x86_capability))) : variable_test_bit(((3*32+26)), ((unsigned long *)((&boot_cpu_data)->x86_capability))))))
		return (x >> 24) & 0xFF;
	else
		return (x >> 24) & 0x0F;
}

/*
 * Warm reset vector default position:
 */



#if definedEx(CONFIG_X86_64)
extern struct apic apic_flat;
extern struct apic apic_physflat;
extern struct apic apic_x2apic_cluster;
extern struct apic apic_x2apic_phys;
extern int default_acpi_madt_oem_check(char *, char *);

extern void apic_send_IPI_self(int vector);

extern struct apic apic_x2apic_uv_x;

#if (definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_x2apic_extra_bits; extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(int) per_cpu__x2apic_extra_bits
#endif
#if (!(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)) && !((definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))))
extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(int) per_cpu__x2apic_extra_bits
#endif
;

extern int default_cpu_present_to_apicid(int mps_cpu);
extern int default_check_phys_apicid_present(int phys_apicid);
#endif
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void default_wait_for_init_deassert(atomic_t *deassert)
{
	while (!atomic_read(deassert))
		cpu_relax();
	return;
}

extern void generic_bigsmp_probe(void);


#if definedEx(CONFIG_X86_LOCAL_APIC)
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/smp.h" 1






























	




	







	
	



	
	
	

	
	

	
	
	
	

	
	













	




	




	




	




	




	




	




	




	




	




	























	

 


	
	













 














	
	





 





#line 466 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apic.h" 2



static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 const struct cpumask *default_target_cpus(void)
{
#if definedEx(CONFIG_SMP)
	return cpu_online_mask;
#endif
#if !(definedEx(CONFIG_SMP))
	return (get_cpu_mask(0));
#endif
}

#if 1
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))

#if (definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_x86_bios_cpu_apicid; extern __attribute__((section(".data.percpu" "")))  __typeof__(u16) per_cpu__x86_bios_cpu_apicid
#endif
#if (!(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)) && !((definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))))
extern __attribute__((section(".data.percpu" "")))  __typeof__(u16) per_cpu__x86_bios_cpu_apicid
#endif
; extern __typeof__(u16) *x86_bios_cpu_apicid_early_ptr; extern __typeof__(u16) x86_bios_cpu_apicid_early_map[]
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))

#if (definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_x86_bios_cpu_apicid; extern __attribute__((section(".data" "")))  __typeof__(u16) per_cpu__x86_bios_cpu_apicid
#endif
#if (!(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)) && !((definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))))
extern __attribute__((section(".data" "")))  __typeof__(u16) per_cpu__x86_bios_cpu_apicid
#endif

#endif
;
#endif

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned int read_apic_id(void)
{
	unsigned int reg;

	reg = apic_read(0x20);

	return apic->get_apic_id(reg);
}

extern void default_setup_apic_routing(void);

extern struct apic apic_noop;


extern struct apic apic_default;

/*
 * Set up the logical destination ID.
 *
 * Intel recommends to set DFR, LDR and TPR before enabling
 * an APIC.  See e.g. "AP-388 82489DX User's Manual" (Intel
 * document number 292116).  So here it goes...
 */
extern void default_init_apic_ldr(void);

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int default_apic_id_registered(void)
{
	return (__builtin_constant_p((read_apic_id())) ? constant_test_bit((read_apic_id()), ((phys_cpu_present_map).mask)) : variable_test_bit((read_apic_id()), ((phys_cpu_present_map).mask)));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int default_phys_pkg_id(int cpuid_apic, int index_msb)
{
	return cpuid_apic >> index_msb;
}

extern int default_apicid_to_node(int logical_apicid);


static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned int
default_cpu_mask_to_apicid(const struct cpumask *cpumask)
{
	return ((cpumask)->bits)[0] & 0xFFu;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned int
default_cpu_mask_to_apicid_and(const struct cpumask *cpumask,
			       const struct cpumask *andmask)
{
	unsigned long mask1 = ((cpumask)->bits)[0];
	unsigned long mask2 = ((andmask)->bits)[0];
	unsigned long mask3 = ((cpu_online_mask)->bits)[0];

	return (unsigned int)(mask1 & mask2 & mask3);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long default_check_apicid_used(physid_mask_t *map, int apicid)
{
	return (__builtin_constant_p((apicid)) ? constant_test_bit((apicid), ((*map).mask)) : variable_test_bit((apicid), ((*map).mask)));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 unsigned long default_check_apicid_present(int bit)
{
	return (__builtin_constant_p((bit)) ? constant_test_bit((bit), ((phys_cpu_present_map).mask)) : variable_test_bit((bit), ((phys_cpu_present_map).mask)));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void default_ioapic_phys_id_map(physid_mask_t *phys_map, physid_mask_t *retmap)
{
	*retmap = *phys_map;
}

/* Mapping from cpu number to logical apicid */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int default_cpu_to_logical_apicid(int cpu)
{
	return 1 << cpu;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int __default_cpu_present_to_apicid(int mps_cpu)
{
	if (mps_cpu < 1 && ((mps_cpu) == 0))
		return (int)
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
(*({ unsigned long __ptr; __asm__ ("" : "=r"(__ptr) : "0"((&per_cpu__x86_bios_cpu_apicid))); (typeof((&per_cpu__x86_bios_cpu_apicid))) (__ptr + (((__per_cpu_offset[mps_cpu])))); }))
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
(*((void)(mps_cpu), &per_cpu__x86_bios_cpu_apicid))
#endif
;
	else
		return 0xFFu;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int
__default_check_phys_apicid_present(int phys_apicid)
{
	return (__builtin_constant_p((phys_apicid)) ? constant_test_bit((phys_apicid), ((phys_cpu_present_map).mask)) : variable_test_bit((phys_apicid), ((phys_cpu_present_map).mask)));
}


static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int default_cpu_present_to_apicid(int mps_cpu)
{
	return __default_cpu_present_to_apicid(mps_cpu);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int
default_check_phys_apicid_present(int phys_apicid)
{
	return __default_check_phys_apicid_present(phys_apicid);
}
 


#endif

extern u8 cpu_2_logical_apicid[1];


#line 15 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/smp.h" 2
#if definedEx(CONFIG_X86_IO_APIC)
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/io_apic.h" 1



#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/types.h" 1



























































































































 

















 



 

















 




	




	



	
	
	
	





#line 6 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/io_apic.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mpspec.h" 1


















 













 
















 



	




	




	









 



 











				   



				 





 

	





	







































	




	
	












	
	












#line 7 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/io_apic.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/apicdef.h" 1




























 




















































































































 
























	

	

	
		
			
			
		
	

	
	
		
			
			
			
		
	

	

	

	

	

	
		
			
		
	

	
	
		
			
		
	

	
	
		
			
		
	

	
		
		
	

	

	
		
			
		
	

	
		
			
		
	

	
		
			
			
			
		
	

	
		
		
	

	
		
		
	

	
		
		
	

	
		
			
				
				
				
				
				
				
				
				
			
		
		
			
			
		
	

	

	

	

	

	

	

	

	
		
			
			
			
			
			
			
			
			
			
		
	

	
		
			
				
				
			
				
		
		
	

	
		
			
			
			
			
			
			
		
	

	
		
			
			
			
			
			
			
		
	

	
		
			
			
			
			
			
			
		
	

	
		
			
			
			
			
			
			
			
			
		
	

	
		
			
			
			
			
			
			
			
			
		
	

	
		
			
			
			
			
			
		
	

	
		
		
	

	
	
		
		
	

	

	

	

	

	
		
			
		
	

	






 
  


#line 8 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/io_apic.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/irq_vectors.h" 1



/*
 * Linux IRQ vector layout.
 *
 * There are 256 IDT entries (per CPU - each entry is 8 bytes) which can
 * be defined by Linux. They are used as a jump table by the CPU when a
 * given vector is triggered - by a CPU-external, CPU-internal or
 * software-triggered event.
 *
 * Linux sets the kernel code address each entry jumps to early during
 * bootup, and never changes them. This is the general layout of the
 * IDT entries:
 *
 *  Vectors   0 ...  31 : system traps and exceptions - hardcoded events
 *  Vectors  32 ... 127 : device interrupts
 *  Vector  128         : legacy int80 syscall interface
 *  Vectors 129 ... 237 : device interrupts
 *  Vectors 238 ... 255 : special interrupts
 *
 * 64-bit x86 has per CPU IDT tables, 32-bit has one shared IDT table.
 *
 * This file enumerates the exact layout of them:
 */




/*
 * IDT vectors usable for external interrupt sources start
 * at 0x20:
 */





 

/*
 * Reserve the lowest usable priority level 0x20 - 0x2f for triggering
 * cleanup after irq migration.
 */


/*
 * Vectors 0x30-0x3f are used for ISA interrupts.
 */


















/*
 * Special IRQ vectors used by the SMP architecture, 0xf0-0xff
 *
 *  some of the following vectors are 'rare', they are merged
 *  into a single vector (CALL_FUNCTION_VECTOR) to save vector space.
 *  TLB, reschedule and local APIC vectors are performance-critical.
 */


/*
 * Sanity check
 */











/* f0-f7 used for spreading out TLB flushes: */




/*
 * Local APIC timer IRQ vector is on a different priority level,
 * to work around the 'lost local interrupt if more than 2 IRQ
 * sources per level' errata.
 */


/*
 * Generic system vector for platform specific use
 */


/*
 * Performance monitoring pending work vector:
 */




/*
 * Self IPI vector for machine checks
 */


/*
 * First APIC vector available to drivers: (vectors 0x30-0xee) we
 * start at 0x31(0x41) to spread out vectors evenly between priority
 * levels. (0x80 is the syscall vector)
 */










static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int invalid_vm86_irq(int irq)
{
	return irq < 3 || irq > 15;
}

/*
 * Size the maximum number of interrupts.
 *
 * If the irq_desc[] array has a sparse layout, we can size things
 * generously - it scales up linearly with the maximum number of CPUs,
 * and the maximum number of IO-APICs, whichever is higher.
 *
 * In other cases we size more conservatively, to not create too large
 * static arrays.
 */






#if definedEx(CONFIG_X86_IO_APIC)
#if definedEx(CONFIG_SPARSE_IRQ)




#endif
#if !(definedEx(CONFIG_SPARSE_IRQ))


 

#endif
#endif



#line 9 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/io_apic.h" 2

/*
 * Intel IO-APIC support for SMP and UP systems.
 *
 * Copyright (C) 1997, 1998, 1999, 2000 Ingo Molnar
 */

/* I/O Unit Redirection Table */








/*
 * The structure of the IO-APIC:
 */
union IO_APIC_reg_00 {
	u32	raw;
	struct {
		u32	__reserved_2	: 14,
			LTS		:  1,
			delivery_type	:  1,
			__reserved_1	:  8,
			ID		:  8;
	} __attribute__ ((packed)) bits;
};

union IO_APIC_reg_01 {
	u32	raw;
	struct {
		u32	version		:  8,
			__reserved_2	:  7,
			PRQ		:  1,
			entries		:  8,
			__reserved_1	:  8;
	} __attribute__ ((packed)) bits;
};

union IO_APIC_reg_02 {
	u32	raw;
	struct {
		u32	__reserved_2	: 24,
			arbitration	:  4,
			__reserved_1	:  4;
	} __attribute__ ((packed)) bits;
};

union IO_APIC_reg_03 {
	u32	raw;
	struct {
		u32	boot_DT		:  1,
			__reserved_1	: 31;
	} __attribute__ ((packed)) bits;
};

enum ioapic_irq_destination_types {
	dest_Fixed = 0,
	dest_LowestPrio = 1,
	dest_SMI = 2,
	dest__reserved_1 = 3,
	dest_NMI = 4,
	dest_INIT = 5,
	dest__reserved_2 = 6,
	dest_ExtINT = 7
};

struct IO_APIC_route_entry {
	__u32	vector		:  8,
		delivery_mode	:  3,	/* 000: FIXED
					 * 001: lowest prio
					 * 111: ExtINT
					 */
		dest_mode	:  1,	/* 0: physical, 1: logical */
		delivery_status	:  1,
		polarity	:  1,
		irr		:  1,
		trigger		:  1,	/* 0: edge, 1: level */
		mask		:  1,	/* 0: enabled, 1: disabled */
		__reserved_2	: 15;

	__u32	__reserved_3	: 24,
		dest		:  8;
} __attribute__ ((packed));

struct IR_IO_APIC_route_entry {
	__u64	vector		: 8,
		zero		: 3,
		index2		: 1,
		delivery_status : 1,
		polarity	: 1,
		irr		: 1,
		trigger		: 1,
		mask		: 1,
		reserved	: 31,
		format		: 1,
		index		: 15;
} __attribute__ ((packed));

#if definedEx(CONFIG_X86_IO_APIC)
/*
 * # of IO-APICs and # of IRQ routing registers
 */
extern int nr_ioapics;
extern int nr_ioapic_registers[64];



/* I/O APIC entries */
extern struct mpc_ioapic mp_ioapics[64];

/* # of MP IRQ source entries */
extern int mp_irq_entries;

/* MP IRQ source entries */
extern struct mpc_intsrc mp_irqs[256];

/* non-0 if default (table-less) MP configuration */
extern int mpc_default_type;

/* Older SiS APIC requires we rewrite the index register */
extern int sis_apic_bug;

/* 1 if "noapic" boot option passed */
extern int skip_ioapic_setup;

/* 1 if "noapic" boot option passed */
extern int noioapicquirk;

/* -1 if "noapic" boot option passed */
extern int noioapicreroute;

/* 1 if the timer IRQ uses the '8259A Virtual Wire' mode */
extern int timer_through_8259;

extern void io_apic_disable_legacy(void);

/*
 * If we use the IO-APIC for IRQ routing, disable automatic
 * assignment of PCI IRQ's.
 */



extern u8 io_apic_unique_id(u8 id);
extern int io_apic_get_unique_id(int ioapic, int apic_id);
extern int io_apic_get_version(int ioapic);
extern int io_apic_get_redir_entries(int ioapic);

struct io_apic_irq_attr;
extern int io_apic_set_pci_routing(struct device *dev, int irq,
		 struct io_apic_irq_attr *irq_attr);
void setup_IO_APIC_irq_extra(u32 gsi);
extern int (*ioapic_renumber_irq)(int ioapic, int irq);
extern void ioapic_init_mappings(void);
extern void ioapic_insert_resources(void);

extern struct IO_APIC_route_entry **alloc_ioapic_entries(void);
extern void free_ioapic_entries(struct IO_APIC_route_entry **ioapic_entries);
extern int save_IO_APIC_setup(struct IO_APIC_route_entry **ioapic_entries);
extern void mask_IO_APIC_setup(struct IO_APIC_route_entry **ioapic_entries);
extern int restore_IO_APIC_setup(struct IO_APIC_route_entry **ioapic_entries);

extern void probe_nr_irqs_gsi(void);

extern int setup_ioapic_entry(int apic, int irq,
			      struct IO_APIC_route_entry *entry,
			      unsigned int destination, int trigger,
			      int polarity, int vector, int pin);
extern void ioapic_write_entry(int apic, int pin,
			       struct IO_APIC_route_entry e);
extern void setup_ioapic_ids_from_mpc(void);

struct mp_ioapic_gsi{
	int gsi_base;
	int gsi_end;
};
extern struct mp_ioapic_gsi  mp_gsi_routing[];
int mp_find_ioapic(int gsi);
int mp_find_ioapic_pin(int ioapic, int gsi);
void __attribute__ ((__section__(".init.text")))  __attribute__((no_instrument_function)) mp_register_ioapic(int id, u32 address, u32 gsi_base);

#endif









#line 17 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/smp.h" 2
#endif
#endif
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/thread_info.h" 1
/* thread_info.h: low-level thread information
 *
 * Copyright (C) 2002  David Howells (dhowells@redhat.com)
 * - Incorporating suggestions made by Linus Torvalds and Dave Miller
 */





















	
	
	
	
	
	
	
	
	

	
	

	


















 






























































































 




















	
		


 









 











	
	
		      
	


 

























	
	
	









#line 20 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/smp.h" 2
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/cpumask.h" 1














#line 21 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/smp.h" 2

extern int smp_num_siblings;
extern unsigned int num_processors;


#if (definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_cpu_sibling_map; extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(cpumask_var_t) per_cpu__cpu_sibling_map
#endif
#if (!(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)) && !((definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))))
extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(cpumask_var_t) per_cpu__cpu_sibling_map
#endif
;

#if (definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_cpu_core_map; extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(cpumask_var_t) per_cpu__cpu_core_map
#endif
#if (!(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)) && !((definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))))
extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(cpumask_var_t) per_cpu__cpu_core_map
#endif
;

#if (definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_cpu_llc_id; extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(u16) per_cpu__cpu_llc_id
#endif
#if (!(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)) && !((definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))))
extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(u16) per_cpu__cpu_llc_id
#endif
;

#if (definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_cpu_number; extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(int) per_cpu__cpu_number
#endif
#if (!(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)) && !((definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))))
extern __attribute__((section(
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
".data.percpu"
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
".data"
#endif
 "")))  __typeof__(int) per_cpu__cpu_number
#endif
;

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 struct cpumask *cpu_sibling_mask(int cpu)
{
	return 
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
(*({ unsigned long __ptr; __asm__ ("" : "=r"(__ptr) : "0"((&per_cpu__cpu_sibling_map))); (typeof((&per_cpu__cpu_sibling_map))) (__ptr + (((__per_cpu_offset[cpu])))); }))
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
(*((void)(cpu), &per_cpu__cpu_sibling_map))
#endif
;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 struct cpumask *cpu_core_mask(int cpu)
{
	return 
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))
(*({ unsigned long __ptr; __asm__ ("" : "=r"(__ptr) : "0"((&per_cpu__cpu_core_map))); (typeof((&per_cpu__cpu_core_map))) (__ptr + (((__per_cpu_offset[cpu])))); }))
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))
(*((void)(cpu), &per_cpu__cpu_core_map))
#endif
;
}

#if 1
#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))

#if (definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_x86_cpu_to_apicid; extern __attribute__((section(".data.percpu" "")))  __typeof__(u16) per_cpu__x86_cpu_to_apicid
#endif
#if (!(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)) && !((definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))))
extern __attribute__((section(".data.percpu" "")))  __typeof__(u16) per_cpu__x86_cpu_to_apicid
#endif
; extern __typeof__(u16) *x86_cpu_to_apicid_early_ptr; extern __typeof__(u16) x86_cpu_to_apicid_early_map[]
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))

#if (definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_x86_cpu_to_apicid; extern __attribute__((section(".data" "")))  __typeof__(u16) per_cpu__x86_cpu_to_apicid
#endif
#if (!(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)) && !((definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))))
extern __attribute__((section(".data" "")))  __typeof__(u16) per_cpu__x86_cpu_to_apicid
#endif

#endif
;


#if (definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))

#if (definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_x86_bios_cpu_apicid; extern __attribute__((section(".data.percpu" "")))  __typeof__(u16) per_cpu__x86_bios_cpu_apicid
#endif
#if (!(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)) && !((definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))))
extern __attribute__((section(".data.percpu" "")))  __typeof__(u16) per_cpu__x86_bios_cpu_apicid
#endif
; extern __typeof__(u16) *x86_bios_cpu_apicid_early_ptr; extern __typeof__(u16) x86_bios_cpu_apicid_early_map[]
#endif
#if (!(definedEx(CONFIG_SMP)) && !((definedEx(CONFIG_SMP) && definedEx(CONFIG_SMP))))

#if (definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_x86_bios_cpu_apicid; extern __attribute__((section(".data" "")))  __typeof__(u16) per_cpu__x86_bios_cpu_apicid
#endif
#if (!(definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)) && !((definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU) && definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU))))
extern __attribute__((section(".data" "")))  __typeof__(u16) per_cpu__x86_bios_cpu_apicid
#endif

#endif
;
#endif


/* Static state in head.S used to set up a CPU */
extern struct {
	void *sp;
	unsigned short ss;
} stack_start;

struct smp_ops {
	void (*smp_prepare_boot_cpu)(void);
	void (*smp_prepare_cpus)(unsigned max_cpus);
	void (*smp_cpus_done)(unsigned max_cpus);

	void (*smp_send_stop)(void);
	void (*smp_send_reschedule)(int cpu);

	int (*cpu_up)(unsigned cpu);
	int (*cpu_disable)(void);
	void (*cpu_die)(unsigned int cpu);
	void (*play_dead)(void);

	void (*send_call_func_ipi)(const struct cpumask *mask);
	void (*send_call_func_single_ipi)(int cpu);
};

/* Globals due to paravirt */
extern void set_cpu_sibling_map(int cpu);

#if definedEx(CONFIG_SMP)



extern struct smp_ops smp_ops;

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void smp_send_stop(void)
{
	smp_ops.smp_send_stop();
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void smp_prepare_boot_cpu(void)
{
	smp_ops.smp_prepare_boot_cpu();
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void smp_prepare_cpus(unsigned int max_cpus)
{
	smp_ops.smp_prepare_cpus(max_cpus);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void smp_cpus_done(unsigned int max_cpus)
{
	smp_ops.smp_cpus_done(max_cpus);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int __cpu_up(unsigned int cpu)
{
	return smp_ops.cpu_up(cpu);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int __cpu_disable(void)
{
	return smp_ops.cpu_disable();
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __cpu_die(unsigned int cpu)
{
	smp_ops.cpu_die(cpu);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void play_dead(void)
{
	smp_ops.play_dead();
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void smp_send_reschedule(int cpu)
{
	smp_ops.smp_send_reschedule(cpu);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void arch_send_call_function_single_ipi(int cpu)
{
	smp_ops.send_call_func_single_ipi(cpu);
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void arch_send_call_function_ipi_mask(const struct cpumask *mask)
{
	smp_ops.send_call_func_ipi(mask);
}

void cpu_disable_common(void);
void native_smp_prepare_boot_cpu(void);
void native_smp_prepare_cpus(unsigned int max_cpus);
void native_smp_cpus_done(unsigned int max_cpus);
int native_cpu_up(unsigned int cpunum);
int native_cpu_disable(void);
void native_cpu_die(unsigned int cpu);
void native_play_dead(void);
void play_dead_common(void);
void wbinvd_on_cpu(int cpu);
int wbinvd_on_all_cpus(void);

void native_send_call_func_ipi(const struct cpumask *mask);
void native_send_call_func_single_ipi(int cpu);

void smp_store_cpu_info(int id);


/* We don't mark CPUs online until __cpu_up(), so we need another measure */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int num_booting_cpus(void)
{
	return cpumask_weight(cpu_callout_mask);
}
#endif
#if !(definedEx(CONFIG_SMP))

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int wbinvd_on_all_cpus(void)
{
	(native_wbinvd());
	return 0;
}
#endif
extern unsigned disabled_cpus __attribute__ ((__section__(".cpuinit.data")));

#if definedEx(CONFIG_X86_32_SMP)
/*
 * This function is needed by all SMP systems. It must _always_ be valid
 * from the initial startup. We map APIC_BASE very early in page_setup(),
 * so this is correct in the x86 case.
 */

extern int safe_smp_processor_id(void);

#endif
#if (definedEx(CONFIG_X86_64_SMP) && !(definedEx(CONFIG_X86_32_SMP)))










#endif
#if definedEx(CONFIG_X86_LOCAL_APIC)
#if !(definedEx(CONFIG_X86_64))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int logical_smp_processor_id(void)
{
	/* we don't want to mark this access volatile - bad code generation */
	return (((apic_read(0xD0)) >> 24) & 0xFFu);
}

#endif
extern int hard_smp_processor_id(void);

#endif
#if !(definedEx(CONFIG_X86_LOCAL_APIC))
#if !(definedEx(CONFIG_SMP))

#endif
#endif


#line 11 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mmzone_32.h" 2

#if definedEx(CONFIG_NUMA)
extern struct pglist_data *node_data[];


#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/numaq.h" 1
/*
 * Written by: Patricia Gaughen, IBM Corporation
 *
 * Copyright (C) 2002, IBM Corp.
 *
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, GOOD TITLE or
 * NON INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * Send feedback to <gone@us.ibm.com>
 */




#if definedEx(CONFIG_X86_NUMAQ)
extern int found_numaq;
extern int get_memcfg_numaq(void);

extern void *xquad_portio;

/*
 * SYS_CFG_DATA_PRIV_ADDR, struct eachquadmem, and struct sys_cfg_data are the
 */


/*
 * Communication area for each processor on lynxer-processor tests.
 *
 * NOTE: If you change the size of this eachproc structure you need
 *       to change the definition for EACH_QUAD_SIZE.
 */
struct eachquadmem {
	unsigned int	priv_mem_start;		/* Starting address of this */
						/* quad's private memory. */
						/* This is always 0. */
						/* In MB. */
	unsigned int	priv_mem_size;		/* Size of this quad's */
						/* private memory. */
						/* In MB. */
	unsigned int	low_shrd_mem_strp_start;/* Starting address of this */
						/* quad's low shared block */
						/* (untranslated). */
						/* In MB. */
	unsigned int	low_shrd_mem_start;	/* Starting address of this */
						/* quad's low shared memory */
						/* (untranslated). */
						/* In MB. */
	unsigned int	low_shrd_mem_size;	/* Size of this quad's low */
						/* shared memory. */
						/* In MB. */
	unsigned int	lmmio_copb_start;	/* Starting address of this */
						/* quad's local memory */
						/* mapped I/O in the */
						/* compatibility OPB. */
						/* In MB. */
	unsigned int	lmmio_copb_size;	/* Size of this quad's local */
						/* memory mapped I/O in the */
						/* compatibility OPB. */
						/* In MB. */
	unsigned int	lmmio_nopb_start;	/* Starting address of this */
						/* quad's local memory */
						/* mapped I/O in the */
						/* non-compatibility OPB. */
						/* In MB. */
	unsigned int	lmmio_nopb_size;	/* Size of this quad's local */
						/* memory mapped I/O in the */
						/* non-compatibility OPB. */
						/* In MB. */
	unsigned int	io_apic_0_start;	/* Starting address of I/O */
						/* APIC 0. */
	unsigned int	io_apic_0_sz;		/* Size I/O APIC 0. */
	unsigned int	io_apic_1_start;	/* Starting address of I/O */
						/* APIC 1. */
	unsigned int	io_apic_1_sz;		/* Size I/O APIC 1. */
	unsigned int	hi_shrd_mem_start;	/* Starting address of this */
						/* quad's high shared memory.*/
						/* In MB. */
	unsigned int	hi_shrd_mem_size;	/* Size of this quad's high */
						/* shared memory. */
						/* In MB. */
	unsigned int	mps_table_addr;		/* Address of this quad's */
						/* MPS tables from BIOS, */
						/* in system space.*/
	unsigned int	lcl_MDC_pio_addr;	/* Port-I/O address for */
						/* local access of MDC. */
	unsigned int	rmt_MDC_mmpio_addr;	/* MM-Port-I/O address for */
						/* remote access of MDC. */
	unsigned int	mm_port_io_start;	/* Starting address of this */
						/* quad's memory mapped Port */
						/* I/O space. */
	unsigned int	mm_port_io_size;	/* Size of this quad's memory*/
						/* mapped Port I/O space. */
	unsigned int	mm_rmt_io_apic_start;	/* Starting address of this */
						/* quad's memory mapped */
						/* remote I/O APIC space. */
	unsigned int	mm_rmt_io_apic_size;	/* Size of this quad's memory*/
						/* mapped remote I/O APIC */
						/* space. */
	unsigned int	mm_isa_start;		/* Starting address of this */
						/* quad's memory mapped ISA */
						/* space (contains MDC */
						/* memory space). */
	unsigned int	mm_isa_size;		/* Size of this quad's memory*/
						/* mapped ISA space (contains*/
						/* MDC memory space). */
	unsigned int	rmt_qmi_addr;		/* Remote addr to access QMI.*/
	unsigned int	lcl_qmi_addr;		/* Local addr to access QMI. */
};

/*
 * Note: This structure must be NOT be changed unless the multiproc and
 * OS are changed to reflect the new structure.
 */
struct sys_cfg_data {
	unsigned int	quad_id;
	unsigned int	bsp_proc_id; /* Boot Strap Processor in this quad. */
	unsigned int	scd_version; /* Version number of this table. */
	unsigned int	first_quad_id;
	unsigned int	quads_present31_0; /* 1 bit for each quad */
	unsigned int	quads_present63_32; /* 1 bit for each quad */
	unsigned int	config_flags;
	unsigned int	boot_flags;
	unsigned int	csr_start_addr; /* Absolute value (not in MB) */
	unsigned int	csr_size; /* Absolute value (not in MB) */
	unsigned int	lcl_apic_start_addr; /* Absolute value (not in MB) */
	unsigned int	lcl_apic_size; /* Absolute value (not in MB) */
	unsigned int	low_shrd_mem_base; /* 0 or 512MB or 1GB */
	unsigned int	low_shrd_mem_quad_offset; /* 0,128M,256M,512M,1G */
					/* may not be totally populated */
	unsigned int	split_mem_enbl; /* 0 for no low shared memory */
	unsigned int	mmio_sz; /* Size of total system memory mapped I/O */
				 /* (in MB). */
	unsigned int	quad_spin_lock; /* Spare location used for quad */
					/* bringup. */
	unsigned int	nonzero55; /* For checksumming. */
	unsigned int	nonzeroaa; /* For checksumming. */
	unsigned int	scd_magic_number;
	unsigned int	system_type;
	unsigned int	checksum;
	/*
	 *	memory configuration area for each quad
	 */
	struct		eachquadmem eq[(1 << 
#if (definedEx(CONFIG_NODES_SHIFT) && definedEx(CONFIG_NODES_SHIFT))
CONFIG_NODES_SHIFT
#endif
#if (!(definedEx(CONFIG_NODES_SHIFT)) && !((definedEx(CONFIG_NODES_SHIFT) && definedEx(CONFIG_NODES_SHIFT))))
0
#endif
)];	/* indexed by quad id */
};

void numaq_tsc_disable(void);

#endif
#if !(definedEx(CONFIG_X86_NUMAQ))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int get_memcfg_numaq(void)
{
	return 0;
}
#endif

#line 17 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mmzone_32.h" 2
/* summit or generic arch */
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/srat.h" 1
/*
 * Some of the code in this file has been gleaned from the 64 bit
 * discontigmem support code base.
 *
 * Copyright (C) 2002, IBM Corp.
 *
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, GOOD TITLE or
 * NON INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * Send feedback to Pat Gaughen <gone@us.ibm.com>
 */




#if definedEx(CONFIG_ACPI_NUMA)
extern int get_memcfg_from_srat(void);
#endif
#if !(definedEx(CONFIG_ACPI_NUMA))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int get_memcfg_from_srat(void)
{
	return 0;
}
#endif

#line 19 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mmzone_32.h" 2

extern int get_memcfg_numa_flat(void);
/*
 * This allows any one NUMA architecture to be compiled
 * for, and still fall back to the flat function if it
 * fails.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void get_memcfg_numa(void)
{

	if (get_memcfg_numaq())
		return;
	if (get_memcfg_from_srat())
		return;
	get_memcfg_numa_flat();
}

extern void resume_map_numa_kva(pgd_t *pgd);

#endif
#if !(definedEx(CONFIG_NUMA))


static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void resume_map_numa_kva(pgd_t *pgd) {}

#endif
#if definedEx(CONFIG_DISCONTIGMEM)
/*
 * generic node memory support, the following assumptions apply:
 *
 * 1) memory comes in 64Mb contiguous chunks which are either present or not
 * 2) we will not have more than 64Gb in total
 *
 * for now assume that 64Gb is max amount of RAM for whole system
 *    64Gb / 4096bytes/page = 16777216 pages
 */




extern s8 physnode_map[];

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int pfn_to_nid(unsigned long pfn)
{
#if definedEx(CONFIG_NUMA)
	return((int) physnode_map[(pfn) / (16777216/1024)]);
#endif
#if !(definedEx(CONFIG_NUMA))
	return 0;
#endif
}

/*
 * Following are macros that each numa implmentation must define.
 */








static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int 
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
((int pfn) < max_pfn)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
((int pfn) < max_mapnr)
#endif

{
	int nid = pfn_to_nid(pfn);

	if (nid >= 0)
		return (pfn < ({ pg_data_t *__pgdat = 
#if (definedEx(CONFIG_NUMA) && definedEx(CONFIG_NEED_MULTIPLE_NODES))
(node_data[nid])
#endif
#if !((definedEx(CONFIG_NEED_MULTIPLE_NODES) && definedEx(CONFIG_NUMA)))
NODE_DATA(nid)
#endif
; __pgdat->node_start_pfn+ __pgdat->node_spanned_pages; }));
	return 0;
}

#endif
#if definedEx(CONFIG_NEED_MULTIPLE_NODES)
/* always use node 0 for bootmem on this numa platform */


#endif

#line 4 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/mmzone.h" 2
 

#line 785 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mmzone.h" 2

#endif
extern struct pglist_data *first_online_pgdat(void);
extern struct pglist_data *next_online_pgdat(struct pglist_data *pgdat);
extern struct zone *next_zone(struct zone *zone);

/**
 * for_each_online_pgdat - helper macro to iterate over all online nodes
 * @pgdat - pointer to a pg_data_t variable
 */




/**
 * for_each_zone - helper macro to iterate over all memory zones
 * @zone - pointer to struct zone variable
 *
 * The user only needs to declare the zone variable, for_each_zone
 * fills it in.
 */













static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 struct zone *zonelist_zone(struct zoneref *zoneref)
{
	return zoneref->zone;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int zonelist_zone_idx(struct zoneref *zoneref)
{
	return zoneref->zone_idx;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int zonelist_node_idx(struct zoneref *zoneref)
{
#if definedEx(CONFIG_NUMA)
	/* zone_to_nid not available in this context */
	return zoneref->zone->node;
#endif
#if !(definedEx(CONFIG_NUMA))
	return 0;
#endif
}

/**
 * next_zones_zonelist - Returns the next zone at or below highest_zoneidx within the allowed nodemask using a cursor within a zonelist as a starting point
 * @z - The cursor used as a starting point for the search
 * @highest_zoneidx - The zone index of the highest zone to return
 * @nodes - An optional nodemask to filter the zonelist with
 * @zone - The first suitable zone found is returned via this parameter
 *
 * This function returns the next zone at or below a given zone index that is
 * within the allowed nodemask using a cursor as the starting point for the
 * search. The zoneref returned is a cursor that represents the current zone
 * being examined. It should be advanced by one before calling
 * next_zones_zonelist again.
 */
struct zoneref *next_zones_zonelist(struct zoneref *z,
					enum zone_type highest_zoneidx,
					nodemask_t *nodes,
					struct zone **zone);

/**
 * first_zones_zonelist - Returns the first zone at or below highest_zoneidx within the allowed nodemask in a zonelist
 * @zonelist - The zonelist to search for a suitable zone
 * @highest_zoneidx - The zone index of the highest zone to return
 * @nodes - An optional nodemask to filter the zonelist with
 * @zone - The first suitable zone found is returned via this parameter
 *
 * This function returns the first zone at or below a given zone index that is
 * within the allowed nodemask. The zoneref returned is a cursor that can be
 * used to iterate the zonelist with next_zones_zonelist by advancing it by
 * one before calling.
 */
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 struct zoneref *first_zones_zonelist(struct zonelist *zonelist,
					enum zone_type highest_zoneidx,
					nodemask_t *nodes,
					struct zone **zone)
{
	return next_zones_zonelist(zonelist->_zonerefs, highest_zoneidx, nodes,
								zone);
}

/**
 * for_each_zone_zonelist_nodemask - helper macro to iterate over valid zones in a zonelist at or below a given zone index and within a nodemask
 * @zone - The current zone in the iterator
 * @z - The current pointer within zonelist->zones being iterated
 * @zlist - The zonelist being iterated
 * @highidx - The zone index of the highest zone to return
 * @nodemask - Nodemask allowed by the allocator
 *
 * This iterator iterates though all zones at or below a given zone index and
 * within a given nodemask
 */





/**
 * for_each_zone_zonelist - helper macro to iterate over valid zones in a zonelist at or below a given zone index
 * @zone - The current zone in the iterator
 * @z - The current pointer within zonelist->zones being iterated
 * @zlist - The zonelist being iterated
 * @highidx - The zone index of the highest zone to return
 *
 * This iterator iterates though all zones at or below a given zone index.
 */



#if definedEx(CONFIG_SPARSEMEM)
#line 1 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/arch/x86/include/asm/sparsemem.h" 1



#if definedEx(CONFIG_SPARSEMEM)
/*
 * generic non-linear memory support:
 *
 * 1) we will not split memory into more chunks than will fit into the flags
 *    field of the struct page
 *
 * SECTION_SIZE_BITS		2^n: size of each section
 * MAX_PHYSADDR_BITS		2^n: max size of physical address space
 * MAX_PHYSMEM_BITS		2^n: how much memory we can have in that space
 *
 */


#if definedEx(CONFIG_X86_PAE)



#endif
#if !(definedEx(CONFIG_X86_PAE))



#endif
 



#endif

#line 909 "/app/home/pgiarrusso/TypeChef/linux-2.6.33.3/include/linux/mmzone.h" 2
#endif



	








#if definedEx(CONFIG_SPARSEMEM)
/*
 * SECTION_SHIFT    		#bits space required to store a section #
 *
 * PA_SECTION_SHIFT		physical address to/from section number
 * PFN_SECTION_SHIFT		pfn to/from section number
 */
















struct page;
struct page_cgroup;
struct mem_section {
	/*
	 * This is, logically, a pointer to an array of struct
	 * pages.  However, it is stored with some other magic.
	 * (see sparse.c::sparse_init_one_section())
	 *
	 * Additionally during early boot we encode node id of
	 * the location of the section here to guide allocation.
	 * (see sparse.c::memory_present())
	 *
	 * Making it a UL at least makes someone do a cast
	 * before using it wrong.
	 */
	unsigned long section_mem_map;

	/* See declaration of similar field in struct zone */
	unsigned long *pageblock_flags;
#if definedEx(CONFIG_CGROUP_MEM_RES_CTLR)
	/*
	 * If !SPARSEMEM, pgdat doesn't have page_cgroup pointer. We use
	 * section. (see memcontrol.h/page_cgroup.h about this.)
	 */
	struct page_cgroup *page_cgroup;
	unsigned long pad;
#endif
};

#if definedEx(CONFIG_SPARSEMEM_EXTREME)

#endif
#if !(definedEx(CONFIG_SPARSEMEM_EXTREME))

#endif




#if definedEx(CONFIG_SPARSEMEM_EXTREME)
extern struct mem_section *mem_section[((1UL << (
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))
36
#endif
#if (!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))))
32
#endif
 - 
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))
29
#endif
#if (!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))))
26
#endif
)) / (((1UL) << 12) / sizeof (struct mem_section)))];
#endif
#if !(definedEx(CONFIG_SPARSEMEM_EXTREME))
extern struct mem_section mem_section[((1UL << (
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))
36
#endif
#if (!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))))
32
#endif
 - 
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))
29
#endif
#if (!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))))
26
#endif
)) / 1)][1];
#endif
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 struct mem_section *__nr_to_section(unsigned long nr)
{
	if (!mem_section[((nr) / 
#if (definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM_EXTREME) && !((definedEx(CONFIG_SPARSEMEM) && !(definedEx(CONFIG_SPARSEMEM_EXTREME)))))
(((1UL) << 12) / sizeof (struct mem_section))
#endif
#if (definedEx(CONFIG_SPARSEMEM) && !(definedEx(CONFIG_SPARSEMEM_EXTREME)) && !((definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM_EXTREME) && !((definedEx(CONFIG_SPARSEMEM) && !(definedEx(CONFIG_SPARSEMEM_EXTREME)))))))
1
#endif
)])
		return ((void *)0);
	return &mem_section[((nr) / 
#if (definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM_EXTREME) && !((definedEx(CONFIG_SPARSEMEM) && !(definedEx(CONFIG_SPARSEMEM_EXTREME)))))
(((1UL) << 12) / sizeof (struct mem_section))
#endif
#if (definedEx(CONFIG_SPARSEMEM) && !(definedEx(CONFIG_SPARSEMEM_EXTREME)) && !((definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM_EXTREME) && !((definedEx(CONFIG_SPARSEMEM) && !(definedEx(CONFIG_SPARSEMEM_EXTREME)))))))
1
#endif
)][nr & (
#if (definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM_EXTREME) && !((definedEx(CONFIG_SPARSEMEM) && !(definedEx(CONFIG_SPARSEMEM_EXTREME)))))
(((1UL) << 12) / sizeof (struct mem_section))
#endif
#if (definedEx(CONFIG_SPARSEMEM) && !(definedEx(CONFIG_SPARSEMEM_EXTREME)) && !((definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM_EXTREME) && !((definedEx(CONFIG_SPARSEMEM) && !(definedEx(CONFIG_SPARSEMEM_EXTREME)))))))
1
#endif
 - 1)];
}
extern int __section_nr(struct mem_section* ms);
extern unsigned long usemap_size(void);

/*
 * We use the lower bits of the mem_map pointer to store
 * a little bit of information.  There should be at least
 * 3 bits here due to 32-bit alignment.
 */






static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 struct page *__section_mem_map_addr(struct mem_section *section)
{
	unsigned long map = section->section_mem_map;
	map &= (~((1UL<<2)-1));
	return (struct page *)map;
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int present_section(struct mem_section *section)
{
	return (section && (section->section_mem_map & (1UL<<0)));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int present_section_nr(unsigned long nr)
{
	return present_section(__nr_to_section(nr));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int valid_section(struct mem_section *section)
{
	return (section && (section->section_mem_map & (1UL<<1)));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int valid_section_nr(unsigned long nr)
{
	return valid_section(__nr_to_section(nr));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 struct mem_section *__pfn_to_section(unsigned long pfn)
{
	return __nr_to_section(((pfn) >> (
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))
29
#endif
#if (!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))))
26
#endif
 - 12)));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int 
#if (definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))
((unsigned long pfn) < max_pfn)
#endif
#if (!(definedEx(CONFIG_X86_64)) && !((definedEx(CONFIG_X86_64) && definedEx(CONFIG_X86_64))))
((unsigned long pfn) < max_mapnr)
#endif

{
	if (((pfn) >> (
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))
29
#endif
#if (!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))))
26
#endif
 - 12)) >= (1UL << (
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))
36
#endif
#if (!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))))
32
#endif
 - 
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))
29
#endif
#if (!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))))
26
#endif
)))
		return 0;
	return valid_section(__nr_to_section(((pfn) >> (
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))
29
#endif
#if (!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))))
26
#endif
 - 12))));
}

static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int pfn_present(unsigned long pfn)
{
	if (((pfn) >> (
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))
29
#endif
#if (!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))))
26
#endif
 - 12)) >= (1UL << (
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))
36
#endif
#if (!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))))
32
#endif
 - 
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))
29
#endif
#if (!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))))
26
#endif
)))
		return 0;
	return present_section(__nr_to_section(((pfn) >> (
#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))
29
#endif
#if (!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM) && !((!(definedEx(CONFIG_X86_PAE)) && definedEx(CONFIG_SPARSEMEM) && definedEx(CONFIG_SPARSEMEM))))))
26
#endif
 - 12))));
}

/*
 * These are _only_ used during initialisation, therefore they
 * can use __initdata ...  They could have names to indicate
 * this restriction.
 */
#if definedEx(CONFIG_NUMA)





#endif
#if !(definedEx(CONFIG_NUMA))

#endif

void sparse_init(void);
#endif
#if !(definedEx(CONFIG_SPARSEMEM))


#endif
#if definedEx(CONFIG_NODES_SPAN_OTHER_NODES)
bool early_pfn_in_nid(unsigned long pfn, int nid);
#endif
#if !(definedEx(CONFIG_NODES_SPAN_OTHER_NODES))

#endif
#if !(definedEx(CONFIG_SPARSEMEM))

#endif
void memory_present(int nid, unsigned long start, unsigned long end);
unsigned long __attribute__ ((__section__(".init.text")))  __attribute__((no_instrument_function)) node_memmap_size_bytes(int, unsigned long, unsigned long);

/*
 * If it is possible to have holes within a MAX_ORDER_NR_PAGES, then we
 * need to check pfn validility within that MAX_ORDER_NR_PAGES block.
 * pfn_valid_within() should be used in this case; we optimise this away
 * when we have no holes within a MAX_ORDER_NR_PAGES block.
 */
#if definedEx(CONFIG_HOLES_IN_ZONE)

#endif
#if !(definedEx(CONFIG_HOLES_IN_ZONE))

#endif
#if definedEx(CONFIG_ARCH_HAS_HOLES_MEMORYMODEL)
/*
 * pfn_valid() is meant to be able to tell if a given PFN has valid memmap
 * associated with it or not. In FLATMEM, it is expected that holes always
 * have valid memmap as long as there is valid PFNs either side of the hole.
 * In SPARSEMEM, it is assumed that a valid section has a memmap for the
 * entire section.
 *
 * However, an ARM, and maybe other embedded architectures in the future
 * free memmap backing holes to save memory on the assumption the memmap is
 * never used. The page_zone linkages are then broken even though pfn_valid()
 * returns true. A walker of the full memmap must then do this additional
 * check to ensure the memmap they are looking at is sane by making sure
 * the zone and PFN linkages are still valid. This is expensive, but walkers
 * of the full memmap are extremely rare.
 */
int memmap_valid_within(unsigned long pfn,
					struct page *page, struct zone *zone);
#endif
#if !(definedEx(CONFIG_ARCH_HAS_HOLES_MEMORYMODEL))
static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 int memmap_valid_within(unsigned long pfn,
					struct page *page, struct zone *zone)
{
	return 1;
}
#endif




/*
 * unshare allows a process to 'unshare' part of the process
 * context which was originally shared using clone.  copy_*
 * functions used by do_fork() cannot be used here directly
 * because they modify an inactive task_struct that is being
 * constructed. Here we are modifying the current, active,
 * task_struct.
 */

#if (definedEx(CONFIG_FTRACE_SYSCALLS) && definedEx(CONFIG_FTRACE_SYSCALLS))
static const char *types__unshare[] = { "unsigned long" }; static const char *args__unshare[] = { "unshare_flags" }; static const struct syscall_metadata __syscall_meta__unshare; static struct ftrace_event_call __attribute__((__aligned__(4))) event_enter__unshare; static struct trace_event enter_syscall_print__unshare = { .trace = print_syscall_enter, }; static struct ftrace_event_call __attribute__((__used__)) __attribute__((__aligned__(4))) __attribute__((section("_ftrace_events"))) event_enter__unshare = { .name = "sys_enter""_unshare", .system = "syscalls", .event = &enter_syscall_print__unshare, .raw_init = trace_event_raw_init, .show_format = syscall_enter_format, .define_fields = syscall_enter_define_fields, .regfunc = reg_event_syscall_enter, .unregfunc = unreg_event_syscall_enter, .data = (void *)&__syscall_meta__unshare, 
#if (definedEx(CONFIG_EVENT_PROFILE) && definedEx(CONFIG_EVENT_PROFILE))
.profile_enable = prof_sysenter_enable, .profile_disable = prof_sysenter_disable,
#endif
#if (!(definedEx(CONFIG_EVENT_PROFILE)) && !((definedEx(CONFIG_EVENT_PROFILE) && definedEx(CONFIG_EVENT_PROFILE))))

#endif
 }; static const struct syscall_metadata __syscall_meta__unshare; static struct ftrace_event_call __attribute__((__aligned__(4))) event_exit__unshare; static struct trace_event exit_syscall_print__unshare = { .trace = print_syscall_exit, }; static struct ftrace_event_call __attribute__((__used__)) __attribute__((__aligned__(4))) __attribute__((section("_ftrace_events"))) event_exit__unshare = { .name = "sys_exit""_unshare", .system = "syscalls", .event = &exit_syscall_print__unshare, .raw_init = trace_event_raw_init, .show_format = syscall_exit_format, .define_fields = syscall_exit_define_fields, .regfunc = reg_event_syscall_exit, .unregfunc = unreg_event_syscall_exit, .data = (void *)&__syscall_meta__unshare, 
#if (definedEx(CONFIG_EVENT_PROFILE) && definedEx(CONFIG_EVENT_PROFILE))
.profile_enable = prof_sysexit_enable, .profile_disable = prof_sysexit_disable,
#endif
#if (!(definedEx(CONFIG_EVENT_PROFILE)) && !((definedEx(CONFIG_EVENT_PROFILE) && definedEx(CONFIG_EVENT_PROFILE))))

#endif
 }; static const struct syscall_metadata __attribute__((__used__)) __attribute__((__aligned__(4))) __attribute__((section("__syscalls_metadata"))) __syscall_meta__unshare = { .name = "sys""_unshare", .nb_args = 1, .types = types__unshare, .args = args__unshare, .enter_event = &event_enter__unshare, .exit_event = &event_exit__unshare, };; 
#if (definedEx(CONFIG_HAVE_SYSCALL_WRAPPERS) && definedEx(CONFIG_HAVE_SYSCALL_WRAPPERS))
 __attribute__((regparm(0))) long sys_unshare(unsigned long unshare_flags); static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 long SYSC_unshare(unsigned long unshare_flags);  __attribute__((regparm(0))) long SyS_unshare(long unshare_flags) { ((void)(sizeof(struct { int:-!!(sizeof(unsigned long) > sizeof(long)); }))); return (long) SYSC_unshare((unsigned long) unshare_flags); } 
#if (definedEx(CONFIG_PPC64) && !((!(definedEx(CONFIG_PPC64)) && (definedEx(CONFIG_MIPS) || definedEx(CONFIG_ALPHA)))) && !((!(definedEx(CONFIG_PPC64)) && !((definedEx(CONFIG_ALPHA) || definedEx(CONFIG_MIPS))))))
asm ("\t.globl " "sys_unshare" "\n\t.set " "sys_unshare" ", " "SyS_unshare" "\n" "\t.globl ." "sys_unshare" "\n\t.set ." "sys_unshare" ", ." "SyS_unshare")
#endif
#if (!((!((definedEx(CONFIG_MIPS) || definedEx(CONFIG_ALPHA))) && !(definedEx(CONFIG_PPC64)))) && (definedEx(CONFIG_ALPHA) || definedEx(CONFIG_MIPS)) && !(definedEx(CONFIG_PPC64)) && !((definedEx(CONFIG_PPC64) && !((!(definedEx(CONFIG_PPC64)) && (definedEx(CONFIG_MIPS) || definedEx(CONFIG_ALPHA)))) && !((!(definedEx(CONFIG_PPC64)) && !((definedEx(CONFIG_ALPHA) || definedEx(CONFIG_MIPS))))))))
asm ( "sys_unshare" " = " "SyS_unshare" "\n\t.globl " "sys_unshare")
#endif
#if (!(definedEx(CONFIG_PPC64)) && !((definedEx(CONFIG_ALPHA) || definedEx(CONFIG_MIPS))) && !((definedEx(CONFIG_PPC64) && !((!(definedEx(CONFIG_PPC64)) && (definedEx(CONFIG_MIPS) || definedEx(CONFIG_ALPHA)))) && !((!(definedEx(CONFIG_PPC64)) && !((definedEx(CONFIG_ALPHA) || definedEx(CONFIG_MIPS))))))) && !((!((!((definedEx(CONFIG_MIPS) || definedEx(CONFIG_ALPHA))) && !(definedEx(CONFIG_PPC64)))) && (definedEx(CONFIG_ALPHA) || definedEx(CONFIG_MIPS)) && !(definedEx(CONFIG_PPC64)))))
asm ("\t.globl " "sys_unshare" "\n\t.set " "sys_unshare" ", " "SyS_unshare")
#endif
; static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 long SYSC_unshare(unsigned long unshare_flags)
#endif
#if (!(definedEx(CONFIG_HAVE_SYSCALL_WRAPPERS)) && !((definedEx(CONFIG_HAVE_SYSCALL_WRAPPERS) && definedEx(CONFIG_HAVE_SYSCALL_WRAPPERS))))
 __attribute__((regparm(0))) long sys_unshare(unsigned long unshare_flags)
#endif

#endif
#if (!(definedEx(CONFIG_FTRACE_SYSCALLS)) && !((definedEx(CONFIG_FTRACE_SYSCALLS) && definedEx(CONFIG_FTRACE_SYSCALLS))))

#if (definedEx(CONFIG_HAVE_SYSCALL_WRAPPERS) && definedEx(CONFIG_HAVE_SYSCALL_WRAPPERS))
 __attribute__((regparm(0))) long sys_unshare(unsigned long unshare_flags); static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 long SYSC_unshare(unsigned long unshare_flags);  __attribute__((regparm(0))) long SyS_unshare(long unshare_flags) { ((void)(sizeof(struct { int:-!!(sizeof(unsigned long) > sizeof(long)); }))); return (long) SYSC_unshare((unsigned long) unshare_flags); } 
#if (definedEx(CONFIG_PPC64) && !((!(definedEx(CONFIG_PPC64)) && (definedEx(CONFIG_MIPS) || definedEx(CONFIG_ALPHA)))) && !((!(definedEx(CONFIG_PPC64)) && !((definedEx(CONFIG_ALPHA) || definedEx(CONFIG_MIPS))))))
asm ("\t.globl " "sys_unshare" "\n\t.set " "sys_unshare" ", " "SyS_unshare" "\n" "\t.globl ." "sys_unshare" "\n\t.set ." "sys_unshare" ", ." "SyS_unshare")
#endif
#if (!((!((definedEx(CONFIG_MIPS) || definedEx(CONFIG_ALPHA))) && !(definedEx(CONFIG_PPC64)))) && (definedEx(CONFIG_ALPHA) || definedEx(CONFIG_MIPS)) && !(definedEx(CONFIG_PPC64)) && !((definedEx(CONFIG_PPC64) && !((!(definedEx(CONFIG_PPC64)) && (definedEx(CONFIG_MIPS) || definedEx(CONFIG_ALPHA)))) && !((!(definedEx(CONFIG_PPC64)) && !((definedEx(CONFIG_ALPHA) || definedEx(CONFIG_MIPS))))))))
asm ( "sys_unshare" " = " "SyS_unshare" "\n\t.globl " "sys_unshare")
#endif
#if (!(definedEx(CONFIG_PPC64)) && !((definedEx(CONFIG_ALPHA) || definedEx(CONFIG_MIPS))) && !((definedEx(CONFIG_PPC64) && !((!(definedEx(CONFIG_PPC64)) && (definedEx(CONFIG_MIPS) || definedEx(CONFIG_ALPHA)))) && !((!(definedEx(CONFIG_PPC64)) && !((definedEx(CONFIG_ALPHA) || definedEx(CONFIG_MIPS))))))) && !((!((!((definedEx(CONFIG_MIPS) || definedEx(CONFIG_ALPHA))) && !(definedEx(CONFIG_PPC64)))) && (definedEx(CONFIG_ALPHA) || definedEx(CONFIG_MIPS)) && !(definedEx(CONFIG_PPC64)))))
asm ("\t.globl " "sys_unshare" "\n\t.set " "sys_unshare" ", " "SyS_unshare")
#endif
; static 
#if !(definedEx(CONFIG_OPTIMIZE_INLINING))
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 long SYSC_unshare(unsigned long unshare_flags)
#endif
#if (!(definedEx(CONFIG_HAVE_SYSCALL_WRAPPERS)) && !((definedEx(CONFIG_HAVE_SYSCALL_WRAPPERS) && definedEx(CONFIG_HAVE_SYSCALL_WRAPPERS))))
 __attribute__((regparm(0))) long sys_unshare(unsigned long unshare_flags)
#endif

#endif

{
	int err = 0;
	struct fs_struct *fs, *new_fs = ((void *)0);
	struct sighand_struct *new_sigh = ((void *)0);
	struct mm_struct *mm, *new_mm = ((void *)0), *active_mm = ((void *)0);
	struct files_struct *fd, *new_fd = ((void *)0);
	struct nsproxy *new_nsproxy = ((void *)0);
	int do_sysvsem = 0;

	check_unshare_flags(&unshare_flags);

	/* Return -EINVAL for all unsupported flags */
	err = -22;
	if (unshare_flags & ~(0x00010000|0x00000200|0x00020000|0x00000800|
				0x00000100|0x00000400|0x00040000|
				0x04000000|0x08000000|0x40000000))
		goto bad_unshare_out;

	/*
	 * CLONE_NEWIPC must also detach from the undolist: after switching
	 * to a new ipc namespace, the semaphore arrays from the old
	 * namespace are unreachable.
	 */
	if (unshare_flags & (0x08000000|0x00040000))
		do_sysvsem = 1;
	if ((err = unshare_thread(unshare_flags)))
		goto bad_unshare_out;
	if ((err = unshare_fs(unshare_flags, &new_fs)))
		goto bad_unshare_cleanup_thread;
	if ((err = unshare_sighand(unshare_flags, &new_sigh)))
		goto bad_unshare_cleanup_fs;
	if ((err = unshare_vm(unshare_flags, &new_mm)))
		goto bad_unshare_cleanup_sigh;
	if ((err = unshare_fd(unshare_flags, &new_fd)))
		goto bad_unshare_cleanup_vm;
	if ((err = unshare_nsproxy_namespaces(unshare_flags, &new_nsproxy,
			new_fs)))
		goto bad_unshare_cleanup_fd;

	if (new_fs ||  new_mm || new_fd || do_sysvsem || new_nsproxy) {
		if (do_sysvsem) {
			/*
			 * CLONE_SYSVSEM is equivalent to sys_exit().
			 */
			exit_sem(get_current());
		}

		if (new_nsproxy) {
			switch_task_namespaces(get_current(), new_nsproxy);
			new_nsproxy = ((void *)0);
		}

		task_lock(get_current());

		if (new_fs) {
			fs = get_current()->fs;
			
#if ((definedEx(CONFIG_SMP) || definedEx(CONFIG_DEBUG_SPINLOCK)) && definedEx(CONFIG_INLINE_WRITE_LOCK) && (definedEx(CONFIG_DEBUG_SPINLOCK) || definedEx(CONFIG_SMP)))
__raw_write_lock(&fs->lock)
#endif
#if (!((definedEx(CONFIG_DEBUG_SPINLOCK) || definedEx(CONFIG_SMP))) && !(((definedEx(CONFIG_SMP) || definedEx(CONFIG_DEBUG_SPINLOCK)) && definedEx(CONFIG_INLINE_WRITE_LOCK) && (definedEx(CONFIG_DEBUG_SPINLOCK) || definedEx(CONFIG_SMP)))))
do { 
#if (definedEx(CONFIG_PREEMPT) && definedEx(CONFIG_PREEMPT))
do { 
#if !((definedEx(CONFIG_PREEMPT_TRACER) || definedEx(CONFIG_DEBUG_PREEMPT)))
do { (current_thread_info()->preempt_count) += (1); } while (0)
#endif
#if (definedEx(CONFIG_DEBUG_PREEMPT) || definedEx(CONFIG_PREEMPT_TRACER))
add_preempt_count(1)
#endif
; __asm__ __volatile__("": : :"memory"); } while (0)
#endif
#if (!(definedEx(CONFIG_PREEMPT)) && !((definedEx(CONFIG_PREEMPT) && definedEx(CONFIG_PREEMPT))))
do { } while (0)
#endif
; (void)0; (void)(&fs->lock); } while (0)
#endif
#if ((definedEx(CONFIG_SMP) || definedEx(CONFIG_DEBUG_SPINLOCK)) && !(((definedEx(CONFIG_SMP) || definedEx(CONFIG_DEBUG_SPINLOCK)) && definedEx(CONFIG_INLINE_WRITE_LOCK) && (definedEx(CONFIG_DEBUG_SPINLOCK) || definedEx(CONFIG_SMP)))))
_raw_write_lock(&fs->lock)
#endif
;
			get_current()->fs = new_fs;
			if (--fs->users)
				new_fs = ((void *)0);
			else
				new_fs = fs;
			
#if ((definedEx(CONFIG_SMP) || definedEx(CONFIG_DEBUG_SPINLOCK)) && (definedEx(CONFIG_DEBUG_SPINLOCK) || definedEx(CONFIG_SMP)))
__raw_write_unlock(&fs->lock)
#endif
#if (!((definedEx(CONFIG_DEBUG_SPINLOCK) || definedEx(CONFIG_SMP))) && !(((definedEx(CONFIG_SMP) || definedEx(CONFIG_DEBUG_SPINLOCK)) && (definedEx(CONFIG_DEBUG_SPINLOCK) || definedEx(CONFIG_SMP)))))
do { 
#if (definedEx(CONFIG_PREEMPT) && definedEx(CONFIG_PREEMPT))
do { do { __asm__ __volatile__("": : :"memory"); 
#if !((definedEx(CONFIG_PREEMPT_TRACER) || definedEx(CONFIG_DEBUG_PREEMPT)))
do { (current_thread_info()->preempt_count) -= (1); } while (0)
#endif
#if (definedEx(CONFIG_DEBUG_PREEMPT) || definedEx(CONFIG_PREEMPT_TRACER))
sub_preempt_count(1)
#endif
; } while (0); __asm__ __volatile__("": : :"memory"); do { if (__builtin_expect(!!(test_ti_thread_flag(current_thread_info(), 3)), 0)) preempt_schedule(); } while (0); } while (0)
#endif
#if (!(definedEx(CONFIG_PREEMPT)) && !((definedEx(CONFIG_PREEMPT) && definedEx(CONFIG_PREEMPT))))
do { } while (0)
#endif
; (void)0; (void)(&fs->lock); } while (0)
#endif
;
		}

		if (new_mm) {
			mm = get_current()->mm;
			active_mm = get_current()->active_mm;
			get_current()->mm = new_mm;
			get_current()->active_mm = new_mm;
			do { paravirt_activate_mm((active_mm), (new_mm)); switch_mm((active_mm), (new_mm), ((void *)0)); } while (0);;
			new_mm = mm;
		}

		if (new_fd) {
			fd = get_current()->files;
			get_current()->files = new_fd;
			new_fd = fd;
		}

		task_unlock(get_current());
	}

	if (new_nsproxy)
		put_nsproxy(new_nsproxy);

bad_unshare_cleanup_fd:
	if (new_fd)
		put_files_struct(new_fd);

bad_unshare_cleanup_vm:
	if (new_mm)
		mmput(new_mm);

bad_unshare_cleanup_sigh:
	if (new_sigh)
		if (atomic_dec_and_test(&new_sigh->count))
			kmem_cache_free(sighand_cachep, new_sigh);

bad_unshare_cleanup_fs:
	if (new_fs)
		free_fs_struct(new_fs);

bad_unshare_cleanup_thread:
bad_unshare_out:
	return err;
}

/*
 *	Helper to unshare the files of the current task.
 *	We don't want to expose copy_files internals to
 *	the exec layer of the kernel.
 */

int unshare_files(struct files_struct **displaced)
{
	struct task_struct *task = get_current();
	struct files_struct *copy = ((void *)0);
	int error;

	error = unshare_fd(0x00000400, &copy);
	if (error || !copy) {
		*displaced = ((void *)0);
		return error;
	}
	*displaced = task->files;
	task_lock(task);
	task->files = copy;
	task_unlock(task);
	return 0;
}
