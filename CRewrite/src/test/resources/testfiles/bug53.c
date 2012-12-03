void foo() {
    if (a) { b; }
    #if definedEx(C)
    else if (d) { e; }
    #endif
    return f;
}