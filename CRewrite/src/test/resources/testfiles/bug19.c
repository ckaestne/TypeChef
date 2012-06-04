int bar() {return 1;}

int hello() {
// copied from coreutils/ls.pi:80305 - 80320
	if (
#if definedEx(A)
	1
#endif
#if !definedEx(A)
	0
#endif
	)

#if !definedEx(B)
	a
#endif
#if definedEx(B)
	b
#endif
	;

	return 1;
}