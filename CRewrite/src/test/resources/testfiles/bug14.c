void foo() {
    int j = 0;
    int i;
    for (i = 0; i < 10; i++) {
        if (i == 8)
            continue;
        j = j + 1;
    }
    return j;
}