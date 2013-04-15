#define X a
#ifdef X

before include

#include "header.h"

after include

#if defined(x) && 3<<4
#ifdef A
	if branch 1
	#include "in2.c"
	#undef X
#elif defined(X) && defined(B)
	if branch 2
    z
#else
	if branch 3
	X
#endif
#endif

#endif
