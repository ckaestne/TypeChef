#if defined(A) && definedEx(B)
void foo() {
	while(!
#if defined(C)
	1
#endif
#if !defined(C)
	0
#endif
()) {
	// do something
	}
}
#endif

void main(){}