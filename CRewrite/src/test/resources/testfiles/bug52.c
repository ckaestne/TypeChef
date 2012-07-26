int foo(int a, int b) {
    int c = a;
    if (c) {
        c += a;
#ifdef A
        c += b;
#endif
    }
    return c;
}