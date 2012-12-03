void foo() {
    for (;1;) {
        a;
        #if definedEx(A)
        break;
        #endif
        b;
    }
    #if !definedEx(A)
    c;
    #endif
    d;
}