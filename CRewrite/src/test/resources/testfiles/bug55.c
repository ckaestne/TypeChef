void foo() {
    #if definedEx(B)
    if (a) {
        b;
        if (
        #if definedEx(C)
        0
        #else
        1
        #endif
        ) {
            d;
        }
        e;
    }
    f;
    #endif
}