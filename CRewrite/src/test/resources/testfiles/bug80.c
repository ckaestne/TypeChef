void foo() {
#if definedEx(A)
	n;
#endif

#if definedEx(B)
#if definedEx(A)
	o;
#endif
	p;
#if definedEx(C)
	q;
#endif
#endif
    r;
}
