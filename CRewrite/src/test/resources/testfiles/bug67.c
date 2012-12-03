void foo() {
    a;
    #if definedEx(X)
    if (b) {
        #if definedEx(Y)
        c;
        #else
        d;
        #endif
    }
    else if (e) { f;}
    #endif
    g;
}