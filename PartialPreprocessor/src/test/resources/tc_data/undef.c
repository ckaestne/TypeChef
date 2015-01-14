
#define X a

#ifdef X
    x should always be defined
	#undef X
#endif

#ifdef X
    x should not be defined
#endif

#ifdef A
	#define X x
#endif

#ifdef B
	#undef X
#endif

#ifdef X
	x should only be defined if <a> and not <b>
#endif
