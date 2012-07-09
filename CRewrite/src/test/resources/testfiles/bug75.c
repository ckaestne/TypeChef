void foo() {
    return (({
        if (a) goto l1;
        b;
    }));
    l1:
    return c;
}