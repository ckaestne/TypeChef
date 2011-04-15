#if defined(SMP)
#define foo(lock, flags) flags = _foo(lock)
#else
//This is not enough to trigger the crash, just the warning:
//#define _foo(lock, flags)	
//While this example, which uses the "missing" argument, is:
#define _foo(lock, flags)	flags = lock
#define foo(lock, flags) _foo(lock, flags)
#endif

#define CALL(lock_cmd) lock_cmd

//foo(bar, flags);

CALL(foo(bar, flags));
