////test from the bnx2.c file in Linux, there the problem occurs on GET_NET_STATS

#define LONG(ctr...)  long(ctr)

LONG(a)

LONG(b,b)


#define VSHORT(c...) short(c)
#define SHORT(c...) VSHORT(c)

SHORT(a)

SHORT(b,b)