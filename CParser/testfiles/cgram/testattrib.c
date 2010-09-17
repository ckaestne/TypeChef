
extern void eprintf(const char *format, ...)
	__attribute__((format(printf, 1, 2)));
	
extern void exit(int)   __attribute__((noreturn));	

extern int square(int n) __attribute__((const));

extern void die(const char *format, ...)
	__attribute__((noreturn))
	__attribute__((format(printf, 1, 2)));
	
void __attribute__((section(".spinlock.text"))) _raw_spin_lock_nest_lock(int *lock, int *map) ;
	