
#define X a

#ifdef X
    X should always be defined
	#undef X
#endif

#ifdef X
    X should not be defined
#endif

#ifdef A
	#define X x
#endif

#ifdef B
	#undef X
#endif

#ifdef X
	X should only be defined if A and not B
#endif
