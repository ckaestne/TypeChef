void foo() {
    a;
    if (
    #if definedEx(A)
    b
    #else
    c
    #endif
    )
    #if definedEx(A)
    d;
    #else
    e;
    #endif
    else f;

    if (g) h;
}