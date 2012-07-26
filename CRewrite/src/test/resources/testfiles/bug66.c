void foo(void) {
    if (a) {
    } else if (b) {
        c;
#if definedEx(A)
    } else if (d) {
        e;
    } else if (f) {
        g;
#endif
    } else
        h;
    if (i -
    #if definedEx(B)
        j
    #else
        k
    #endif
    - l
    )
        m;
}
