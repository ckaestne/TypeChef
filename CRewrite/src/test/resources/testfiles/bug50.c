void foo() {
    #if definedEx(A)
    while (a) {
        b;
        if (c) break;
        d;
    }
    #endif

    #if definedEx(A)
    if (
    #if definedEx(A) && definedEx(B)
    k
    #else
    l
    #endif
    ) {
        m;
    }
    #endif
}