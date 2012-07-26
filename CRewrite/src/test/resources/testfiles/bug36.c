void foo() {
    if (a) {
        b;
    } else if (
    #if definedEx(A)
        c
    #else
        d
    #endif
    ) {
        e;
    } else if (
    #if definedEx(B)
        f
    #else
        g
    #endif
    ) {
        h;
    } else {
        i;
    }
    j;
}