#if definedEx(A)
void foo() {
    a;
    b;
    c;
    #if !definedEx(A)
    d;
    #endif
    e;
}
#endif