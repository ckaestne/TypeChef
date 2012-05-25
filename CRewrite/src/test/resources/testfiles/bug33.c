void foo() {
    a;
#if definedEx(A)
    b;
#else
    c;
#endif
#if definedEx(A)
    d;
#else
    e;
#endif
    f;
}