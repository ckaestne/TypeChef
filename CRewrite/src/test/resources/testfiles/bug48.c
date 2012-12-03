void foo() {
    if (
    #if definedEx(A)
    c
    #else
    d
    #endif
    ) {
        e;
    }

    #if definedEx(A)
    a;
    #else
    b;
    #endif

    f;
}