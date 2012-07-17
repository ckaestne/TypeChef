void foo() {
    a;
    #if definedEx(A)
    if (
    #if definedEx(B)
    b
    #else
    c
    #endif
    )
      d;
    #endif
    d;
}