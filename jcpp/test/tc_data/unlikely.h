
#ifdef X
#define unlikely(x)	__builtin_expect(!!(x), 0)
#else
#define unlikely(x)	(__builtin_constant_p(x) ? !!(x) : __branch_check__(x, 0))
#endif

#ifdef Z
#define MAX_REG_OFFSET (offsetof(struct pt_regs, ss)) 
#endif


static inline unsigned long regs_get_register(struct pt_regs *regs,
					      unsigned int offset)
{
	if (unlikely(offset > MAX_REG_OFFSET))
		return 0;
	return *(unsigned long *)((unsigned long)regs + offset);
}

