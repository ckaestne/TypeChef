void foo() {
    if (a) {
        foo01();
    }
    else if (b) {
        if (c) foo02();
        foo03();
    }
    else {
        if (d) foo04();
        foo05();
    }
    foo06();
}