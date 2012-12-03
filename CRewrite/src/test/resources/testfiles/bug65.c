void foo() {
    for (a; b; c) {
        #if definedEx(A)
        d;
        #endif
    }
}