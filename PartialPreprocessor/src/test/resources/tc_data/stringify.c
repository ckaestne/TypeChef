#ifdef FLAG
#define A B
#else
#define A C
#endif

#define _STR(a) bla #a
#define STR(a) _STR(a)

STR(A)