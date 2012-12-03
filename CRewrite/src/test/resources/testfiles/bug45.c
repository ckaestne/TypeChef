void foo() {
    #if definedEx(A)
    a;
    #endif

    #if definedEx(B)
    #if definedEx(A)
    b;
    #endif
    c;
    #endif

    #if definedEx(C)
    #if definedEx(A)||definedEx(B)
    d;
    #endif
    e;
    #endif

    #if definedEx(A)||definedEx(B)||definedEx(C)
    f;
    #endif

    g;
}