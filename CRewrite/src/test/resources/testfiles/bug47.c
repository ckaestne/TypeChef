void foo() {
    a;
    #if definedEx(A)
    b;
    #endif
    #if definedEx(A)||definedEx(B)
    c;
    #endif
    #if definedEx(A)
    d;
    #endif
    e;
}