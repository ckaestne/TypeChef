#if definedEx(A)
void foo() {
    #if definedEx(B)
    return ({
        a;
        b;
    });
    #else
    return ({
        c;
        d;
    });
    #endif
}
#endif