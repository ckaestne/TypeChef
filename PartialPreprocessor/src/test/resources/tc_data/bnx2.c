////test from the bnx2.c file in Linux, there the problem occurs on GET_NET_STATS

#define LONG(ctr)  long(ctr)

#define SHORT(ctr)  short(ctr)

#ifdef AA
#define DECIDE(c)   LONG(c)
#else
#define DECIDE(c)   SHORT(c)
#endif

DECIDE(a)

DECIDE(b)
