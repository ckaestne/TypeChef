//from header.h


#ifdef CONFIG_PRINTK
asmlinkage int vprintk(const char *fmt, va_list args)
	__attribute__ ((format (printf, 1, 0)));
asmlinkage int printk(const char * fmt, ...)
	__attribute__ ((format (printf, 1, 2))) __cold;

extern int __printk_ratelimit(const char *func);
#define printk_ratelimit() __printk_ratelimit(__func__)
extern bool printk_timed_ratelimit(unsigned long *caller_jiffies,
				   unsigned int interval_msec);

extern int printk_delay_msec;

/*
 * Print a one-time message (analogous to WARN_ONCE() et al):
 */
#define printk_once(x...) ({			\
	static bool __print_once;		\
						\
	if (!__print_once) {			\
		__print_once = true;		\
		printk(x);			\
	}					\
})

void log_buf_kexec_setup(void);
#else
static inline int vprintk(const char *s, va_list args)
	__attribute__ ((format (printf, 1, 0)));
static inline int vprintk(const char *s, va_list args) { return 0; }
static inline int printk(const char *s, ...)
	__attribute__ ((format (printf, 1, 2)));
static inline int __cold printk(const char *s, ...) { return 0; }
static inline int printk_ratelimit(void) { return 0; }
static inline bool printk_timed_ratelimit(unsigned long *caller_jiffies, \
					  unsigned int interval_msec)	\
		{ return false; }

/* No effect, but we still get type checking even in the !PRINTK case: */
#define printk_once(x...) printk(x)

static inline void log_buf_kexec_setup(void)
{
}
#endif