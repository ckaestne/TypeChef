#if defined A
#define XA
#define GLOBAL
#elif defined B
#define XB
#define GLOBAL
#define GLOBAL
#else
#define XD
#endif

#ifdef XA
printxa
#endif
#ifdef XB
printxb
#endif
#ifdef XD
printxd
#endif
#ifdef GLOBAL
printglobal
#endif
