#if definedEx(A)
#if definedEx(B)
struct testAB;
#endif
#if !definedEx(B)
struct test_AnB;
#endif
#endif

#if definedEx(A)
#if definedEx(B)
struct _AB;
#endif
#if !definedEx(B)
struct _AnB;
#endif
#endif

