void foo() {
    if (a) {
        b;
    }
    #if definedEx(A)
    else if (c) {
        d;
    }
    #endif

    #if definedEx(A)
    if (e) {
        f;
    }
    #endif
    g;
}