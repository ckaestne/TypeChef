void foo() {
#if definedEx(A)
    a;

    if (b)
#endif
    {
        c;
        bar(
        #if definedEx(C)
        2
        #endif
        );

        if (
#if definedEx(B)
1
#endif
#if !definedEx(B)
0
#endif
)
        c;
    }

    d;
}