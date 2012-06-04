////test from the bnx2.c file in Linux

#define LONG(ctr)  long(ctr)

#define SHORT(ctr)  short(ctr)

#ifdef AA
#define DECIDE   LONG
#else
#define DECIDE   SHORT
#endif

DECIDE(a)

DECIDE(b)
