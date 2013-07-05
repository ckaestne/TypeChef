// . -bug
#line 12 "/local/TypeChef-Linux34-Analysis/TypeChef-LinuxAnalysis/linux-3.4/arch/x86/include/asm/ptrace.h" 2
#if definedEx(CONFIG_X86_32)
/* this struct defines the way the registers are stored on the
   stack during a system call. */
 struct pt_regs {
	unsigned long bx;
	unsigned long cx;
	unsigned long dx;
	unsigned long si;
	unsigned long di;
	unsigned long bp;
	unsigned long ax;
	unsigned long ds;
	unsigned long es;
	unsigned long fs;
	unsigned long gs;
	unsigned long orig_ax;
	unsigned long ip;
	unsigned long cs;
	unsigned long flags;
	unsigned long sp;
	unsigned long ss;
};
#endif
#if !definedEx(CONFIG_X86_32)
 struct pt_regs {
	unsigned long r15;
	unsigned long r14;
	unsigned long r13;
	unsigned long r12;
	unsigned long bp;
	unsigned long bx;
/* arguments: non interrupts/non tracing syscalls only save up to here*/
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
#if !definedEx(CONFIG_OPTIMIZE_INLINING)
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline __attribute__((no_instrument_function))
#endif
 unsigned long regs_get_register(struct pt_regs *regs,
					      unsigned int offset)
{
	if (__builtin_expect(!!(offset > (__builtin_offsetof(struct pt_regs,ss))), 0))
		return 0;
	return *(unsigned long *)((unsigned long)regs + offset);
}


//  , -bug
typedef struct cpumask { unsigned long bits[(((
#if definedEx(CONFIG_SMP)
8
#endif
#if !definedEx(CONFIG_SMP)
1
#endif
) + (8 * sizeof(long)) - 1) / (8 * sizeof(long)))]; } cpumask_t;
static 
#if !definedEx(CONFIG_OPTIMIZE_INLINING)
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline __attribute__((no_instrument_function))
#endif
 int bitmap_subset(const unsigned long *src1,
			const unsigned long *src2, int nbits)
{
	if ((__builtin_constant_p(nbits) && (nbits) <= 
#if definedEx(CONFIG_64BIT)
64
#endif
#if !definedEx(CONFIG_64BIT)
32
#endif
))
		return ! ((*src1 & ~(*src2)) & ( ((nbits) % 
#if definedEx(CONFIG_64BIT)
64
#endif
#if !definedEx(CONFIG_64BIT)
32
#endif
) ? (1UL<<((nbits) % 
#if definedEx(CONFIG_64BIT)
64
#endif
#if !definedEx(CONFIG_64BIT)
32
#endif
))-1 : ~0UL ));
	else
		return bitmap_subset(src1, src2, nbits);
}
#if definedEx(CONFIG_SMP) || definedEx(CONFIG_D)
int nr_cpu_ids;
#endif
/**
 * cpumask_subset - (*src1p & ~*src2p) == 0
 * @src1p: the first input
 * @src2p: the second input
 */
static 
#if !definedEx(CONFIG_OPTIMIZE_INLINING)
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline __attribute__((no_instrument_function))
#endif
 int cpumask_subset(const struct cpumask *src1p,
				 const struct cpumask *src2p)
{
	return bitmap_subset(((src1p)->bits), ((src2p)->bits),
						  
#if definedEx(CONFIG_CPUMASK_OFFSTACK)

#if !definedEx(CONFIG_SMP)
1
#endif
#if definedEx(CONFIG_SMP)
nr_cpu_ids
#endif

#endif
#if !definedEx(CONFIG_CPUMASK_OFFSTACK)

#if definedEx(CONFIG_SMP)
8
#endif
#if !definedEx(CONFIG_SMP)
1
#endif

#endif
);
}

int f(int x 
#if definedEx(CONFIG_D)
, int y
#endif
) {
	return x;
}

 int cpumask_subset_myOwn(const struct cpumask *src1p,
				 const struct cpumask *src2p)
{
	unsigned int *aux;
	unsigned long low, high;
	asm volatile(".byte 0x0f,0x01,0xf9"
		     : "=a" (low), "=d" (high), "=c" (*aux));
	return f(
#if definedEx(CONFIG_D)
nr_cpu_ids ,
#endif
 3
);
}
