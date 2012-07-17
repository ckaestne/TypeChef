void foo() {
    a;
    #if definedEx(A)
    b;
    #else
    c;
    #endif
    d;
    e;
    do {
        #if definedEx(A)
        if (f) {
            g;
            h;
        }
        #endif
    } while (i);
    j;
}