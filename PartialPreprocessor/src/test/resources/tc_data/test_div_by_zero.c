// expect cpp failure

#define F (1 / 0)
F
#if 1
A
#else
B
#endif
#if F
A
#else
B
#endif
