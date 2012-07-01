void foo() {
    for (a; b; c)
    #if definedEx(A)
        d;
    #else
        e;
    #endif

    f;
}