void foo() {
    #if definedEx(A)
    a;
    #endif

    #if definedEx(B)
        #if definedEx(A)
        b;
        #endif
        #if !definedEx(A)
        c;
        #endif
        d;
    #endif

    e;
}