void foo() {
    if (c) {
        e;
        #if definedEx(A)
        f;
        #endif
    }
    #if definedEx(A)
    g;
    #else
    h;
    #endif

    return i;
}