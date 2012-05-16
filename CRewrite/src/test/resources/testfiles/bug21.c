void foo01() {
    if (k) {
        goto test01;
    }
    test01: ;
    return 0;
}

void foo02() {
    if (k) {
        goto test01;
    }
    test02: ;
    return 0;
}