void foo() {
    a;
    #if definedEx(X)
    b;
    #endif
    #if definedEx(X) && definedEx(Y)
    c;
    #endif
    #if definedEx(X)
    d;
    #endif
    e;
}