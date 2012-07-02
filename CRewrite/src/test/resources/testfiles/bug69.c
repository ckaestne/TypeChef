#if definedEx(A)
void foo() {
    if (a) { b; }
    else
    #if definedEx(B) && definedEx(C)
    do {
        c;
    } while (d)
    #else
    #if definedEx(B) && !definedEx(C)
    do {
        e;
    } while (f)
    #else
    do {
        g;
    } while (h)
    #endif
    #endif
}
#endif