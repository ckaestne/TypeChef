int bar() {
	return 5;
}
#ifdef A
int foo() {
#ifdef B
	int opts = bar();
#endif
	return 5;
}
#endif

void main() {}