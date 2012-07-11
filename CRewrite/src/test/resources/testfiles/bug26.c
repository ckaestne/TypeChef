void foo() {
    if (a) { foo01(); }
    else if (b) { foo02(); }
#if definedEx(A)
    else if (c) { foo03(); }
#endif
    else foo04();

    if (z) { spam01(); }
}