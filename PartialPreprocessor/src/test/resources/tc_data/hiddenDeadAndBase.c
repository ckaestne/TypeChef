#ifdef sometimes
some
#if 1
always someti
#endif
#if 0
never someti
#endif
#endif

#define tautology
#ifdef tautology
taut!
#endif

#undef contradiction
#ifdef contradiction
contr!
#endif

#ifdef tautology
x
#elif defined contradiction
z
#else
y
#endif

#ifdef sometimes
s
#elif defined contradiction
t
#else
u
#endif

#if 1
always
#endif

#if 0
never
#endif


