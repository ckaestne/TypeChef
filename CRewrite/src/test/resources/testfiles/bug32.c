void foo() {
    a;
#if !definedEx(A)
    b;
#endif

#if (!definedEx(A))&(!definedEx(B))
    if (c) {
        d;
    }
#endif
    e;
}