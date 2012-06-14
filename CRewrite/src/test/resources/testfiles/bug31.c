void foo() {
    if (a) {
        switch (b) {
            case 4: case 3: case 1: case 254: goto next;
            case 2: foo01();
        }
    }

    foo02();
    next:
    foo03();
}