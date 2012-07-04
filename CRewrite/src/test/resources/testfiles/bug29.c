void foo() {
    a;
#if definedEx(A)
    if (b) { foo01(); }
    else if (c) { foo02(); }
#if definedEx(B)
    else if (d) { foo03(); }
#else
    else if (e) { foo04(); }
#endif
#if definedEx(B)
    else if (f) { foo05(); }
#endif
    else if (g) { foo06(); }
    else foo07();
#endif
    h;
}